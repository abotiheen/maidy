package com.example.maidy.feature_maid.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.model.ChatMessage
import com.example.maidy.core.service.MaidGeminiChatService
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class MaidChatViewModel(
    private val geminiChatService: MaidGeminiChatService
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    init {
        // Show Gemini welcome message on entry
        if (_messages.value.isEmpty()) {
            _messages.value = listOf(
                ChatMessage(
                    content = """
                    Hi! I'm your Maidy assistant 👋
                    
                    I can:
                    • Retrieve your current bookings and upcoming schedule.
                    • Help you find details for a specific job (date, customer, status).
                    • Update the status of a specific booking when you tell me the Booking ID (for example: confirm a booking).
                    
                    Try: "Show my bookings" or "Confirm booking 12345".
                """.trimIndent(),
                    timestamp = Timestamp(Date()),
                    isFromUser = false
                )
            )
        }
    }

    fun sendMessage(message: String) {
        val now = Timestamp(Date())
        val chatMsg = ChatMessage(
            content = message,
            timestamp = now,
            isFromUser = true
        )
        _messages.value = _messages.value + chatMsg
        _isProcessing.value = true
        // Show Gemini loading bubble
        val loadingMsg = ChatMessage(
            content = "Thinking...",
            timestamp = Timestamp(Date()),
            isFromUser = false,
            isLoading = true
        )
        _messages.value = _messages.value + loadingMsg
        viewModelScope.launch {
            val response = geminiChatService.processMessage(message)
            _isProcessing.value = false
            val answer = response.getOrElse { "Sorry, an error occurred. Please try again." }
            // Replace the last Gemini bubble with the actual answer
            _messages.value = _messages.value.dropLast(1) + ChatMessage(
                content = answer,
                timestamp = Timestamp(Date()),
                isFromUser = false
            )
        }
    }

    fun clearMessages() {
        _messages.value = emptyList()
        geminiChatService.clearHistory()
    }
}
