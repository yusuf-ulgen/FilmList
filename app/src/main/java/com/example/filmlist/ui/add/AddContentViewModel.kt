package com.example.filmlist.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddContentViewModel(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userLists = MutableStateFlow<List<UserList>>(emptyList())
    val userLists = _userLists.asStateFlow()

    private val _saveStatus = MutableSharedFlow<Boolean>()
    val saveStatus = _saveStatus.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    init {
        loadUserLists()
    }

    private fun loadUserLists() {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null) {
                userDao.getUserLists(userId).collect {
                    _userLists.value = it
                }
            }
        }
    }

    fun saveMediaContent(title: String, type: String, rating: Int, comment: String?, listId: Long) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
                val mediaContent = MediaContent(
                    userId = userId,
                    listId = listId,
                    title = title,
                    type = type,
                    date = date,
                    rating = rating,
                    comment = comment
                )
                userDao.insertMediaContent(mediaContent)
                _saveStatus.emit(true)
            } else {
                _error.emit("Oturum açılamadı. Lütfen giriş yapın.")
            }
        }
    }
}
