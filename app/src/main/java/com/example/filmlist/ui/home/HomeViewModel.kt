package com.example.filmlist.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.remote.Movie
import com.example.filmlist.data.repository.MovieRepository
import com.example.filmlist.data.repository.ChatRepository
import com.example.filmlist.data.local.UserDao
import com.example.filmlist.data.local.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class FeedItem {
    data class MovieDiscovery(val movie: Movie, val isAiSuggested: Boolean) : FeedItem()
}

class HomeViewModel(
    private val repository: MovieRepository,
    private val chatRepository: ChatRepository,
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _feedItems = MutableStateFlow<List<FeedItem>>(emptyList())
    val feedItems = _feedItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    private var currentPage = 1
    private var isAiPhaseFinished = false
    private val aiLimit = 25

    init {
        loadInitialFeed()
    }

    private fun loadInitialFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                val userHistory = userDao.getUserMediaContentSync(userId).map { it.title }
                if (userHistory.isNotEmpty()) {
                    // Try AI Phase first
                    val aiSuggestions = fetchAiSuggestions(userHistory)
                    if (aiSuggestions.isNotEmpty()) {
                        _feedItems.value = aiSuggestions.take(aiLimit).map { FeedItem.MovieDiscovery(it, true) }
                    } else {
                        isAiPhaseFinished = true
                        loadPopularMovies()
                    }
                } else {
                    isAiPhaseFinished = true
                    loadPopularMovies()
                }
            } else {
                isAiPhaseFinished = true
                loadPopularMovies()
            }
            _isLoading.value = false
        }
    }

    private suspend fun fetchAiSuggestions(history: List<String>): List<Movie> {
        // AI suggestions logic - this would ideally return a list of Movie objects
        // For now, we mock the transition after the first batch
        return emptyList() // TODO: Implement AI title-to-movie matching
    }

    fun loadMore() {
        if (_isLoading.value) return
        viewModelScope.launch {
            if (_feedItems.value.size >= aiLimit) {
                isAiPhaseFinished = true
            }

            if (isAiPhaseFinished) {
                loadPopularMovies()
            }
        }
    }

    private suspend fun loadPopularMovies() {
        _isLoading.value = true
        // Randomize the first page to get different movies each time
        if (currentPage == 1) {
            currentPage = (1..20).random()
        }
        repository.getPopularMovies(currentPage)
            .onSuccess { movies ->
                val newItems = movies.map { FeedItem.MovieDiscovery(it, false) }
                _feedItems.value = _feedItems.value + newItems
                currentPage++
            }
            .onFailure { exception ->
                _error.emit(exception.message ?: "Beklenmedik bir hata oluştu.")
            }
        _isLoading.value = false
    }
}
