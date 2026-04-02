package com.example.filmlist.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.repository.AuthRepository
import com.example.filmlist.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        addMessage(text, true)
        
        viewModelScope.launch {
            _isLoading.value = true
            val response = chatRepository.sendMessage(text)
            if (response != null) {
                addMessage(response, false)
            } else {
                _error.emit("Yapay zeka yanıt veremedi. Lütfen tekrar deneyin.")
            }
            _isLoading.value = false
        }
    }

    fun getRecommendations() {
        viewModelScope.launch {
            val userId = authRepository.sessionManager.userId.first()
            if (userId != -1L) {
                _isLoading.value = true
                val userMovies = authRepository.userDao.getUserMediaContentSync(userId).map { it.title }
                val response = chatRepository.getRecommendations(userMovies)
                if (response != null) {
                    addMessage(response, false)
                } else {
                    _error.emit("Öneri alınamadı.")
                }
                _isLoading.value = false
            } else {
                _error.emit("Kullanıcı oturumu bulunamadı.")
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        _messages.value = _messages.value + ChatMessage(text, isUser)
    }
}
