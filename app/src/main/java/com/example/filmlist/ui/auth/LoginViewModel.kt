package com.example.filmlist.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableSharedFlow<Boolean>()
    val loginResult = _loginResult.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun login(email: String, password: String, rememberMe: Boolean) {
        if (email.isEmpty() || password.isEmpty()) {
            viewModelScope.launch { _error.emit("Lütfen tüm alanları doldurun.") }
            return
        }

        viewModelScope.launch {
            val success = repository.login(email, password, rememberMe)
            if (success) {
                _loginResult.emit(true)
            } else {
                _error.emit("E-posta veya şifre hatalı.")
            }
        }
    }
}
