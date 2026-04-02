package com.example.filmlist.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.local.MediaContent
import com.example.filmlist.data.local.SessionManager
import com.example.filmlist.data.local.UserDao
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddContentViewModel(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _contentSaved = MutableSharedFlow<Boolean>()
    val contentSaved = _contentSaved.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun saveContent(
        title: String,
        type: String,
        date: String,
        rating: Int,
        comment: String?,
        isSpoiler: Boolean
    ) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                val newContent = MediaContent(
                    userId = userId,
                    title = title,
                    type = type,
                    date = date,
                    rating = rating,
                    comment = comment,
                    isSpoiler = isSpoiler
                )
                userDao.insertMediaContent(newContent)
                _contentSaved.emit(true)
            } else {
                _error.emit("Oturum hatası. Lütfen tekrar giriş yapın.")
            }
        }
    }
}
