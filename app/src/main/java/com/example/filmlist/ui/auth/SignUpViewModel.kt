package com.example.filmlist.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _signUpResult = MutableSharedFlow<Boolean>()
    val signUpResult = _signUpResult.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun signUp(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            viewModelScope.launch { _error.emit("Lütfen tüm alanları doldurun.") }
            return
        }

        viewModelScope.launch {
            val success = repository.signUp(email, password)
            if (success) {
                _signUpResult.emit(true)
            } else {
                _error.emit("Bu e-posta adresi ile zaten bir kullanıcı kayıtlı.")
            }
        }
    }
}
