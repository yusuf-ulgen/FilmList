package com.example.filmlist.data.repository

import com.example.filmlist.BuildConfig
import com.example.filmlist.data.remote.*
import com.example.filmlist.data.local.MovieDao
import com.example.filmlist.data.local.MovieEntity

class MovieRepository(private val movieDao: MovieDao) {
    private val api = RetrofitInstance.tmdbApi
    private val apiKey = BuildConfig.TMDB_API_KEY

    suspend fun getPopularMovies(): Result<List<Movie>> {
        return try {
            val response = api.getPopularMovies(apiKey)
            if (response.results.isNotEmpty()) {
                // Cache results
                movieDao.clearMovies()
                movieDao.insertMovies(response.results.map { it.toEntity() })
                Result.success(response.results)
            } else {
                Result.failure(Exception("Hiç film bulunamadı."))
            }
        } catch (e: Exception) {
            // Fallback to cache
            val cached = movieDao.getAllMoviesBySync() // Need to add this helper or use first()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toDomain() })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getMovieVideoKey(movieId: Int): String? {
        return try {
            val response = api.getMovieVideos(movieId, apiKey)
            response.results.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }?.key
                ?: response.results.firstOrNull { it.site == "YouTube" }?.key
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchMovies(query: String): Result<List<Movie>> {
        return try {
            val response = api.searchMovies(apiKey, query)
            if (response.results.isNotEmpty()) {
                Result.success(response.results)
            } else {
                Result.failure(Exception("Sonuç bulunamadı."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Movie.toEntity() = MovieEntity(id, title, overview, posterPath, releaseDate, voteAverage)
    private fun MovieEntity.toDomain() = Movie(id, title, overview, posterPath, releaseDate, voteAverage)
}
