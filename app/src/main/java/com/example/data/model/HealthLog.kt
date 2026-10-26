package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class HealthLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val date: String = "",
    val systolic: Int = 120, // BP systolic
    val diastolic: Int = 80, // BP diastolic
    val heartRate: Int = 72, // bpm
    val bloodSugar: Int = 95, // mg/dL fasting
    val weight: Double = 65.0, // kg
    val waterIntakeMl: Int = 2000, // ml
    val sleepHours: Double = 7.5, // hrs
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
