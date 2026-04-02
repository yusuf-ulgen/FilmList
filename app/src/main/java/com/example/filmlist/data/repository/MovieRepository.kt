package com.example.filmlist.data.repository

import com.example.filmlist.BuildConfig
import com.example.filmlist.data.remote.Movie
import com.example.filmlist.data.remote.RetrofitInstance

class MovieRepository {
    private val api = RetrofitInstance.tmdbApi
    private val apiKey = BuildConfig.TMDB_API_KEY

    suspend fun getPopularMovies(): List<Movie>? {
        return try {
            val response = api.getPopularMovies(apiKey)
            response.results
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchMovies(query: String): List<Movie>? {
        return try {
            val response = api.searchMovies(apiKey, query)
            response.results
        } catch (e: Exception) {
            null
        }
    }
}
