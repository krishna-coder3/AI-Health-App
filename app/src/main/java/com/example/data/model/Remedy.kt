package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Remedy(
    @DocumentId val id: String = "",
    val nameEn: String = "",
    val nameHi: String = "",
    val category: String = "",
    val ingredients: List<String> = emptyList(),
    val method: List<String> = emptyList(),
    val benefits: List<String> = emptyList(),
    val precautions: List<String> = emptyList(),
    val whoShouldAvoid: List<String> = emptyList(),
    val doctorNote: String = "",
    val isDesi: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
