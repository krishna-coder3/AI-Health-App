package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val role: String = "user", // "user" or "owner"
    val displayName: String = "",
    val age: Int = 0,
    val gender: String = "Other",
    val height: Double = 0.0, // cm
    val weight: Double = 0.0, // kg
    val allergies: String = "",
    val conditions: String = "",
    val languagePreference: String = "English", // "Hindi", "English", "Hinglish"
    val isBlocked: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val bmi: Double
        get() {
            if (height <= 0 || weight <= 0) return 0.0
            val heightM = height / 100.0
            return (weight / (heightM * heightM) * 10.0).toInt() / 10.0
        }

    val bmiCategory: String
        get() = when {
            bmi <= 0.0 -> "Not Set"
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal weight"
            bmi < 29.9 -> "Overweight"
            else -> "Obese"
        }
}
