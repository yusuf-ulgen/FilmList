package com.example.filmlist.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.remote.Movie
import com.example.filmlist.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies = _popularMovies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    init {
        getPopularMovies()
    }

    fun getPopularMovies() {
        _isLoading.value = true
        viewModelScope.launch {
            val movies = repository.getPopularMovies()
            if (movies != null) {
                _popularMovies.value = movies
            } else {
                _error.emit("Filmler yüklenemedi. Lütfen internetinizi kontrol edin.")
            }
            _isLoading.value = false
        }
    }
}
