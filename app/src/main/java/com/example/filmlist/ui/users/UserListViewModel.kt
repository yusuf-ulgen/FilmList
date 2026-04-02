package com.example.filmlist.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserListViewModel(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userMediaContent = MutableStateFlow<List<MediaContent>>(emptyList())
    val userMediaContent = _userMediaContent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadUserList()
    }

    fun loadUserList() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                userDao.getUserMediaContent(userId).collect { content ->
                    _userMediaContent.value = content
                    _isLoading.value = false
                }
            } else {
                _isLoading.value = false
            }
        }
    }
}
