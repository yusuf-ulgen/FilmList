package com.example.filmlist.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.local.SessionManager
import com.example.filmlist.data.local.UserDao
import com.example.filmlist.data.repository.StatsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userDao: UserDao,
    val sessionManager: SessionManager,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _stats = MutableStateFlow<StatsRepository.UserStats?>(null)
    val stats = _stats.asStateFlow()

    private val _username = sessionManager.userEmail
    val username = _username

    val profileImageUri = sessionManager.profileImageUri

    init {
        observeStats()
    }

    private fun observeStats() {
        viewModelScope.launch {
            sessionManager.userId.collectLatest { userId ->
                if (userId != null && userId != -1L) {
                    userDao.getUserMediaContent(userId).collectLatest {
                        _stats.value = statsRepository.getUserStats(userId)
                    }
                }
            }
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                _stats.value = statsRepository.getUserStats(userId)
            }
        }
    }

    fun saveProfileImage(uri: String) {
        viewModelScope.launch {
            sessionManager.saveProfileImage(uri)
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }
}
