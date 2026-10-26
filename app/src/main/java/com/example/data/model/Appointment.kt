package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Appointment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val specialty: String = "",
    val date: String = "",
    val timeSlot: String = "",
    val status: String = "booked", // "booked", "cancelled", "rescheduled", "completed"
    val notes: String = "",
    val fee: Int = 500,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
