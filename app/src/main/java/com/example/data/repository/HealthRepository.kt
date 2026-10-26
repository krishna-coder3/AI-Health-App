package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Announcement
import com.example.data.model.Appointment
import com.example.data.model.AppSettings
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.Doctor
import com.example.data.model.HealthLog
import com.example.data.model.MedicalReport
import com.example.data.model.Remedy
import com.example.data.model.UserProfile
import com.example.data.seed.SeedDoctors
import com.example.data.seed.SeedRemedies
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "HealthRepository"
const val OWNER_EMAIL = "krrrsingh190@gmail.com"

class HealthRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun isCurrentUserOwner(): Boolean {
        val email = auth.currentUser?.email
        return email != null && email.equals(OWNER_EMAIL, ignoreCase = true)
    }

    // --- User Profile ---

    suspend fun getOrCreateUserProfile(): UserProfile {
        val uid = requireUserId()
        val email = auth.currentUser?.email.orEmpty()
        val displayName = auth.currentUser?.displayName.orEmpty()
        val userDoc = db.collection("users").document(uid)

        try {
            val snapshot = userDoc.get().await()
            if (snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java)
                if (profile != null) {
                    // Check if role should be upgraded to owner
                    val isOwner = email.equals(OWNER_EMAIL, ignoreCase = true)
                    if (isOwner && profile.role != "owner") {
                        userDoc.update("role", "owner").await()
                        return profile.copy(role = "owner")
                    }
                    return profile
                }
            }

            // Create initial profile
            val role = if (email.equals(OWNER_EMAIL, ignoreCase = true)) "owner" else "user"
            val newProfile = UserProfile(
                userId = uid,
                email = email,
                role = role,
                displayName = displayName.ifBlank { "User" },
                age = 28,
                gender = "Other",
                height = 170.0,
                weight = 65.0,
                allergies = "None",
                conditions = "None",
                languagePreference = "English",
                isBlocked = false
            )

            val payload = mapOf(
                "userId" to uid,
                "email" to email,
                "role" to role,
                "displayName" to newProfile.displayName,
                "age" to newProfile.age,
                "gender" to newProfile.gender,
                "height" to newProfile.height,
                "weight" to newProfile.weight,
                "allergies" to newProfile.allergies,
                "conditions" to newProfile.conditions,
                "languagePreference" to newProfile.languagePreference,
                "isBlocked" to false,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            userDoc.set(payload).await()
            return newProfile
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, userDoc.path)
            throw e
        }
    }

    fun observeUserProfile(): Flow<UserProfile?> = flow {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        docRef.snapshots()
            .map { it.toObject(UserProfile::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.GET, docRef.path)
                emit(null)
            }
            .collect { emit(it) }
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val updates = mutableMapOf<String, Any>(
                "displayName" to profile.displayName,
                "age" to profile.age,
                "gender" to profile.gender,
                "height" to profile.height,
                "weight" to profile.weight,
                "allergies" to profile.allergies,
                "conditions" to profile.conditions,
                "languagePreference" to profile.languagePreference,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updates).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun getAllUsers(): List<UserProfile> {
        val path = "users"
        return try {
            val querySnapshot = db.collection(path).get().await()
            querySnapshot.toObjects(UserProfile::class.java)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            emptyList()
        }
    }

    suspend fun setUserBlockedStatus(targetUserId: String, isBlocked: Boolean) {
        val docRef = db.collection("users").document(targetUserId)
        try {
            docRef.update("isBlocked", isBlocked, "updatedAt", FieldValue.serverTimestamp()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- Remedies Catalog ---

    fun observeRemedies(): Flow<List<Remedy>> = flow {
        val path = "remedies"
        db.collection(path)
            .snapshots()
            .map { snapshot ->
                val list = snapshot.toObjects(Remedy::class.java)
                if (list.isEmpty()) {
                    SeedRemedies.remedies
                } else {
                    list
                }
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(SeedRemedies.remedies)
            }
            .collect { emit(it) }
    }

    suspend fun seedRemediesIfEmpty() {
        if (!isCurrentUserOwner()) return
        val path = "remedies"
        try {
            val existing = db.collection(path).limit(1).get().await()
            if (existing.isEmpty) {
                Log.d(TAG, "Seeding remedies...")
                val batch = db.batch()
                for (remedy in SeedRemedies.remedies) {
                    val ref = db.collection(path).document(remedy.id)
                    val payload = mapOf(
                        "nameEn" to remedy.nameEn,
                        "nameHi" to remedy.nameHi,
                        "category" to remedy.category,
                        "ingredients" to remedy.ingredients,
                        "method" to remedy.method,
                        "benefits" to remedy.benefits,
                        "precautions" to remedy.precautions,
                        "whoShouldAvoid" to remedy.whoShouldAvoid,
                        "doctorNote" to remedy.doctorNote,
                        "isDesi" to remedy.isDesi,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    batch.set(ref, payload)
                }
                batch.commit().await()
                Log.d(TAG, "Remedies seeded successfully.")
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
        }
    }

    suspend fun addRemedy(remedy: Remedy) {
        val path = "remedies"
        val docRef = if (remedy.id.isNotBlank()) db.collection(path).document(remedy.id) else db.collection(path).document()
        try {
            val payload = mapOf(
                "nameEn" to remedy.nameEn,
                "nameHi" to remedy.nameHi,
                "category" to remedy.category,
                "ingredients" to remedy.ingredients,
                "method" to remedy.method,
                "benefits" to remedy.benefits,
                "precautions" to remedy.precautions,
                "whoShouldAvoid" to remedy.whoShouldAvoid,
                "doctorNote" to remedy.doctorNote,
                "isDesi" to remedy.isDesi,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteRemedy(remedyId: String) {
        val docRef = db.collection("remedies").document(remedyId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- Doctors Catalog ---

    fun observeDoctors(): Flow<List<Doctor>> = flow {
        val path = "doctors"
        db.collection(path)
            .snapshots()
            .map { snapshot ->
                val list = snapshot.toObjects(Doctor::class.java)
                if (list.isEmpty()) {
                    SeedDoctors.doctors
                } else {
                    list
                }
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(SeedDoctors.doctors)
            }
            .collect { emit(it) }
    }

    suspend fun seedDoctorsIfEmpty() {
        if (!isCurrentUserOwner()) return
        val path = "doctors"
        try {
            val existing = db.collection(path).limit(1).get().await()
            if (existing.isEmpty) {
                Log.d(TAG, "Seeding doctors...")
                val batch = db.batch()
                for (doc in SeedDoctors.doctors) {
                    val ref = db.collection(path).document(doc.id)
                    val payload = mapOf(
                        "name" to doc.name,
                        "specialty" to doc.specialty,
                        "fee" to doc.fee,
                        "rating" to doc.rating,
                        "experienceYears" to doc.experienceYears,
                        "hospital" to doc.hospital,
                        "bio" to doc.bio,
                        "availableSlots" to doc.availableSlots,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    batch.set(ref, payload)
                }
                batch.commit().await()
                Log.d(TAG, "Doctors seeded successfully.")
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
        }
    }

    suspend fun addDoctor(doctor: Doctor) {
        val path = "doctors"
        val docRef = if (doctor.id.isNotBlank()) db.collection(path).document(doctor.id) else db.collection(path).document()
        try {
            val payload = mapOf(
                "name" to doctor.name,
                "specialty" to doctor.specialty,
                "fee" to doctor.fee,
                "rating" to doctor.rating,
                "experienceYears" to doctor.experienceYears,
                "hospital" to doctor.hospital,
                "bio" to doctor.bio,
                "availableSlots" to doctor.availableSlots,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteDoctor(doctorId: String) {
        val docRef = db.collection("doctors").document(doctorId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- Appointments ---

    fun observeMyAppointments(): Flow<List<Appointment>> = flow {
        val uid = requireUserId()
        val path = "appointments"
        db.collection(path)
            .whereEqualTo("userId", uid)
            .snapshots()
            .map { it.toObjects(Appointment::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    fun observeAllAppointments(): Flow<List<Appointment>> = flow {
        val path = "appointments"
        db.collection(path)
            .snapshots()
            .map { it.toObjects(Appointment::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun bookAppointment(
        doctorId: String,
        doctorName: String,
        specialty: String,
        date: String,
        timeSlot: String,
        fee: Int,
        notes: String
    ): String {
        val uid = requireUserId()
        val email = auth.currentUser?.email.orEmpty()
        val name = auth.currentUser?.displayName.orEmpty().ifBlank { "Patient" }
        val docRef = db.collection("appointments").document()

        try {
            val payload = mapOf(
                "userId" to uid,
                "userName" to name,
                "userEmail" to email,
                "doctorId" to doctorId,
                "doctorName" to doctorName,
                "specialty" to specialty,
                "date" to date,
                "timeSlot" to timeSlot,
                "status" to "booked",
                "notes" to notes,
                "fee" to fee,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            return docRef.id
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateAppointmentStatus(appointmentId: String, newStatus: String) {
        val docRef = db.collection("appointments").document(appointmentId)
        try {
            docRef.update(
                "status", newStatus,
                "updatedAt", FieldValue.serverTimestamp()
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun rescheduleAppointment(appointmentId: String, newDate: String, newSlot: String) {
        val docRef = db.collection("appointments").document(appointmentId)
        try {
            docRef.update(
                "date", newDate,
                "timeSlot", newSlot,
                "status", "rescheduled",
                "updatedAt", FieldValue.serverTimestamp()
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- Health Logs ---

    fun observeHealthLogs(): Flow<List<HealthLog>> = flow {
        val uid = requireUserId()
        val path = "healthLogs"
        db.collection(path)
            .whereEqualTo("userId", uid)
            .snapshots()
            .map { it.toObjects(HealthLog::class.java).sortedByDescending { log -> log.date } }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun addHealthLog(log: HealthLog) {
        val uid = requireUserId()
        val docRef = db.collection("healthLogs").document()
        try {
            val payload = mapOf(
                "userId" to uid,
                "date" to log.date,
                "systolic" to log.systolic,
                "diastolic" to log.diastolic,
                "heartRate" to log.heartRate,
                "bloodSugar" to log.bloodSugar,
                "weight" to log.weight,
                "waterIntakeMl" to log.waterIntakeMl,
                "sleepHours" to log.sleepHours,
                "notes" to log.notes,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteHealthLog(logId: String) {
        val docRef = db.collection("healthLogs").document(logId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- AI Chat History ---

    suspend fun loadChatMessages(): List<ChatMessage> {
        val uid = requireUserId()
        val docRef = db.collection("chats").document(uid)
        return try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val json = snapshot.getString("messagesJson") ?: "[]"
                parseChatMessagesJson(json)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            emptyList()
        }
    }

    suspend fun saveChatMessages(messages: List<ChatMessage>) {
        val uid = requireUserId()
        val docRef = db.collection("chats").document(uid)
        try {
            val json = serializeChatMessagesJson(messages)
            val payload = mapOf(
                "userId" to uid,
                "messagesJson" to json,
                "title" to "Health Chat",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun clearChatHistory() {
        val uid = requireUserId()
        val docRef = db.collection("chats").document(uid)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    private fun serializeChatMessagesJson(messages: List<ChatMessage>): String {
        val array = JSONArray()
        for (msg in messages) {
            val obj = JSONObject().apply {
                put("id", msg.id)
                put("sender", msg.sender)
                put("text", msg.text)
                put("timestamp", msg.timestamp)
                put("isEmergency", msg.isEmergency)
            }
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseChatMessagesJson(json: String): List<ChatMessage> {
        val list = mutableListOf<ChatMessage>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ChatMessage(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        sender = obj.optString("sender", "user"),
                        text = obj.optString("text", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isEmergency = obj.optBoolean("isEmergency", false)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed parsing chat messages json", e)
        }
        return list
    }

    // --- Medical Reports ---

    fun observeMedicalReports(): Flow<List<MedicalReport>> = flow {
        val uid = requireUserId()
        val path = "reports"
        db.collection(path)
            .whereEqualTo("userId", uid)
            .snapshots()
            .map { it.toObjects(MedicalReport::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun saveMedicalReport(report: MedicalReport): String {
        val uid = requireUserId()
        val docRef = db.collection("reports").document()
        try {
            val payload = mapOf(
                "userId" to uid,
                "title" to report.title,
                "summaryText" to report.summaryText,
                "abnormalValues" to report.abnormalValues,
                "suggestedQuestions" to report.suggestedQuestions,
                "imageUrl" to report.imageUrl,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            return docRef.id
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteMedicalReport(reportId: String) {
        val docRef = db.collection("reports").document(reportId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- Announcements ---

    fun observeAnnouncements(): Flow<List<Announcement>> = flow {
        val path = "announcements"
        db.collection(path)
            .snapshots()
            .map { it.toObjects(Announcement::class.java).sortedByDescending { a -> a.date } }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
            .collect { emit(it) }
    }

    suspend fun publishAnnouncement(title: String, content: String) {
        val email = auth.currentUser?.email.orEmpty()
        val docRef = db.collection("announcements").document()
        try {
            val payload = mapOf(
                "title" to title,
                "content" to content,
                "date" to java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                "authorEmail" to email,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    // --- App Settings ---

    fun observeAppSettings(): Flow<AppSettings> = flow {
        val docRef = db.collection("appSettings").document("global")
        docRef.snapshots()
            .map { snapshot ->
                snapshot.toObject(AppSettings::class.java) ?: AppSettings()
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.GET, docRef.path)
                emit(AppSettings())
            }
            .collect { emit(it) }
    }

    suspend fun updateAppSettings(settings: AppSettings) {
        val docRef = db.collection("appSettings").document("global")
        try {
            val payload = mapOf(
                "aiEnabled" to settings.aiEnabled,
                "maintenanceMode" to settings.maintenanceMode,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }
}
