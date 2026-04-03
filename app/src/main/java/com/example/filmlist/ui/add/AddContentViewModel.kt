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

    private val _contentSaved = MutableSharedFlow<Boolean>()
    val contentSaved = _contentSaved.asSharedFlow()

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

    fun saveContent(title: String, type: String, date: String, rating: Int, comment: String?, isSpoiler: Boolean) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                // Find or create a default list for the user if userLists is empty
                var targetListId = _userLists.value.firstOrNull()?.id
                
                if (targetListId == null) {
                    val defaultList = UserList(userId = userId, name = "İzlediklerim", orderIndex = 0)
                    targetListId = userDao.insertUserList(defaultList)
                }

                val mediaContent = MediaContent(
                    userId = userId,
                    listId = targetListId,
                    title = title,
                    type = type,
                    date = date,
                    rating = rating,
                    comment = comment
                    // isSpoiler normally goes here if entity supports it
                )
                userDao.insertMediaContent(mediaContent)
                _contentSaved.emit(true)
            } else {
                _error.emit("Oturum açılamadı. Lütfen giriş yapın.")
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
                _contentSaved.emit(true)
            } else {
                _error.emit("Oturum açılamadı. Lütfen giriş yapın.")
            }
        }
    }
}
