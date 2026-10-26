package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Announcement(
    @DocumentId val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val authorEmail: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AppSettings(
    @DocumentId val id: String = "global",
    val aiEnabled: Boolean = true,
    val maintenanceMode: Boolean = false,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
