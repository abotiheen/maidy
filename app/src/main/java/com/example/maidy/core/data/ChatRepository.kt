package com.example.maidy.core.data

import com.example.maidy.core.model.ChatMessage
import com.example.maidy.core.service.GeminiChatService
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Repository for managing chat state and interactions
 */
class ChatRepository(
    private val geminiChatService: GeminiChatService
) {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    /**
     * Send a user message and get AI response
     */
    suspend fun sendMessage(content: String): Result<Unit> {
        return try {
            // Add user message
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = content,
                isFromUser = true,
                timestamp = Timestamp.now()
            )
            _messages.value = _messages.value + userMessage

            // Add loading indicator
            val loadingMessage = ChatMessage(
                id = "loading",
                content = "",
                isFromUser = false,
                timestamp = Timestamp.now(),
                isLoading = true
            )
            _messages.value = _messages.value + loadingMessage

            // Get AI response
            val responseResult = geminiChatService.processMessage(content)

            // Remove loading indicator
            _messages.value = _messages.value.filter { it.id != "loading" }

            if (responseResult.isSuccess) {
                val aiMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = responseResult.getOrNull()
                        ?: "Sorry, I couldn't generate a response.",
                    isFromUser = false,
                    timestamp = Timestamp.now()
                )
                _messages.value = _messages.value + aiMessage
                Result.success(Unit)
            } else {
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = "I'm having trouble responding right now. Please try again!",
                    isFromUser = false,
                    timestamp = Timestamp.now()
                )
                _messages.value = _messages.value + errorMessage
                Result.failure(responseResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Clear chat history
     */
    fun clearChat() {
        _messages.value = emptyList()
    }

    /**
     * Initialize chat with welcome message
     */
    fun initializeChat() {
        if (_messages.value.isEmpty()) {
            val welcomeMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "👋 Hi! I'm your Maidy AI assistant. I can help you with:\n\n" +
                        "📅 Checking your bookings and their status\n" +
                        "🌟 Finding the best and highest-rated maids\n\n" +
                        "Try asking:\n" +
                        "• \"Show me my bookings\"\n" +
                        "• \"Who are the best maids?\"\n" +
                        "• \"What's my booking status?\"\n\n" +
                        "What can I help you with today?",
                isFromUser = false,
                timestamp = Timestamp.now()
            )
            _messages.value = listOf(welcomeMessage)
        }
    }
}
