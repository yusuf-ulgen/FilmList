package com.yusufulgen.filmlist.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yusufulgen.filmlist.data.remote.Movie
import com.yusufulgen.filmlist.data.repository.MovieRepository
import com.yusufulgen.filmlist.data.repository.ChatRepository
import com.yusufulgen.filmlist.data.local.UserDao
import com.yusufulgen.filmlist.data.local.SessionManager
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

    private var currentPage = (1..20).random()
    private var isAiPhaseFinished = false
    private val aiLimit = 25

    init {
        loadInitialFeed()
    }

    fun refreshMovies() {
        currentPage = (1..20).random()
        isAiPhaseFinished = false
        _feedItems.value = emptyList()
        loadInitialFeed()
    }

    private fun loadInitialFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                val userHistory = userDao.getUserMediaContentSync(userId).map { it.title }
                if (userHistory.isNotEmpty()) {
                    val aiSuggestions = fetchAiSuggestions(userHistory)
                    if (aiSuggestions.isNotEmpty()) {
                        _feedItems.value = aiSuggestions.take(aiLimit).map { FeedItem.MovieDiscovery(it, true) }
                        // If we got fewer than 5 AI results, might as well finish AI phase
                        if (aiSuggestions.size < 5) {
                            isAiPhaseFinished = true
                        }
                    } else {
                        isAiPhaseFinished = true
                        loadPopularMovies()
                    }
                } else {
                    isAiPhaseFinished = true
                    loadPopularMovies()
                }
                // Once initial feed is done (either AI or Popular), next loads should be Popular
                if (_feedItems.value.isNotEmpty() && _feedItems.value.size < aiLimit) {
                    // Optional: we can fill up to 25 with popular right away if needed,
                    // but usually loadMore is fine.
                }
            } else {
                isAiPhaseFinished = true
                loadPopularMovies()
            }
            _isLoading.value = false
        }
    }

    private suspend fun fetchAiSuggestions(history: List<String>): List<Movie> {
        val aiResponse = chatRepository.getRecommendations(history) ?: return emptyList()
        val suggestedTitles = aiResponse.lines()
            .filter { line -> line.any { it.isDigit() } || line.contains("-") || line.contains(".") }
            .map { line -> 
                line.replace(Regex("^[0-9.\\-\\s]+"), "").trim() 
            }
            .filter { it.isNotBlank() && it.length > 2 }
            .distinct()
            .take(20)

        val suggestedMovies = mutableListOf<Movie>()
        for (title in suggestedTitles) {
            val result = repository.searchMovies(title)
            result.onSuccess { movies ->
                movies.firstOrNull()?.let { suggestedMovies.add(it) }
            }
        }
        return suggestedMovies
    }

    fun loadMore() {
        if (_isLoading.value) return
        viewModelScope.launch {
            // If we have items but isn't marked as finished, mark it now to allow popular loading
            if (!isAiPhaseFinished) {
                isAiPhaseFinished = true
            }
            loadPopularMovies()
        }
    }

    private suspend fun loadPopularMovies() {
        _isLoading.value = true
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
