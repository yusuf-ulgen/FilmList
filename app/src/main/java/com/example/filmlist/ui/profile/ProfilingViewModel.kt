package com.example.filmlist.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.local.SessionManager
import com.example.filmlist.data.local.UserDao
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfilingViewModel(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _profileSaved = MutableSharedFlow<Boolean>()
    val profileSaved = _profileSaved.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun saveProfile(nickname: String) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                val user = userDao.getUserById(userId).first()
                if (user != null) {
                    val updatedUser = user.copy(nickname = nickname)
                    userDao.updateUser(updatedUser)
                    _profileSaved.emit(true)
                }
            } else {
                _error.emit("Oturum hatası, tekrar giriş yapın.")
            }
        }
    }
}
