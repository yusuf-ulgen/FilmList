package com.example.filmlist.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.local.*
import com.example.filmlist.data.remote.Movie
import com.example.filmlist.data.repository.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddContentViewModel(
    private val userDao: UserDao,
    private val sessionManager: SessionManager,
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _userLists = MutableStateFlow<List<UserList>>(emptyList())
    val userLists = _userLists.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

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
                    _userLists.value = it.filter { list -> list.id != -1L }
                }
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            movieRepository.searchMovies(query).onSuccess {
                _searchResults.value = it
            }.onFailure {
                _searchResults.value = emptyList()
            }
        }
    }

    // Legacy method for AddFilmActivity compatibility
    fun saveContent(title: String, type: String, date: String, rating: Int, comment: String?, isSpoiler: Boolean) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                // If no list is specified, use the first one or create default
                val lists = userDao.getUserLists(userId).first()
                val targetListId = lists.firstOrNull { it.id != -1L }?.id ?: run {
                    val defaultList = UserList(userId = userId, name = "İzlediklerim", orderIndex = 0)
                    userDao.insertUserList(defaultList)
                }

                val mediaContent = MediaContent(
                    userId = userId,
                    listId = targetListId,
                    title = title,
                    type = type,
                    date = date,
                    rating = rating,
                    comment = comment
                )
                userDao.insertMediaContent(mediaContent)
                _contentSaved.emit(true)
            }
        }
    }

    fun saveMediaContent(title: String, type: String, rating: Int, comment: String?, listId: Long) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                val count = userDao.countMediaInListByTitle(listId, title)
                if (count > 0) {
                    _error.emit("Bu içerik seçili listede zaten mevcut.")
                    return@launch
                }

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
