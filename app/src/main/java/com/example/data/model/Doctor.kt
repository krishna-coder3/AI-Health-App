package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Doctor(
    @DocumentId val id: String = "",
    val name: String = "",
    val specialty: String = "",
    val fee: Int = 500,
    val rating: Double = 4.8,
    val experienceYears: Int = 10,
    val hospital: String = "",
    val bio: String = "",
    val availableSlots: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
