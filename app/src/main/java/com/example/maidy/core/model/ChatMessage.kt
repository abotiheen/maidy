package com.example.maidy.core.model

import com.google.firebase.Timestamp

/**
 * Represents a chat message in the conversation
 */
data class ChatMessage(
    val id: String = "",
    val content: String = "",
    val isFromUser: Boolean = true,
    val timestamp: Timestamp = Timestamp.now(),
    val isLoading: Boolean = false
)

/**
 * Types of intents the AI can detect
 */
enum class ChatIntent {
    QUERY_BOOKINGS,           // User asking about their bookings
    SEARCH_MAIDS,             // User searching for maids
    UNKNOWN                   // Could not determine intent
}
