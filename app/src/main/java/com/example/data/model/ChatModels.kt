package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String = "user", // "user" or "model" or "system"
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isEmergency: Boolean = false
)

@IgnoreExtraProperties
data class ChatSession(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "Health Chat",
    val messagesJson: String = "[]",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
