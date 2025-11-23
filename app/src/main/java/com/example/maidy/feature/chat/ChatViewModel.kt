package com.example.maidy.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.ChatRepository
import com.example.maidy.core.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the chat screen
 */
class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val messages: StateFlow<List<ChatMessage>> = chatRepository.messages

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    init {
        chatRepository.initializeChat()
    }

    /**
     * Send a message to the AI assistant
     */
    fun sendMessage(content: String) {
        if (content.isBlank() || _isProcessing.value) return

        viewModelScope.launch {
            _isProcessing.value = true
            chatRepository.sendMessage(content)
            _isProcessing.value = false
        }
    }

    /**
     * Clear chat history
     */
    fun clearChat() {
        chatRepository.clearChat()
        chatRepository.initializeChat()
    }
}
