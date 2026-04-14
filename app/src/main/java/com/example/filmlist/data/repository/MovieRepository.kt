package com.example.filmlist.data.repository

import com.example.filmlist.BuildConfig
import com.example.filmlist.data.remote.*
import com.example.filmlist.data.local.MovieDao
import com.example.filmlist.data.local.MovieEntity

class MovieRepository(private val movieDao: MovieDao) {
    private val api = RetrofitInstance.tmdbApi
    private val apiKey = BuildConfig.TMDB_API_KEY

    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>> {
        if (apiKey == "YOUR_TMDB_API_KEY_HERE") {
            return Result.failure(Exception("API anahtarı eksik! Lütfen local.properties dosyasını kontrol edin."))
        }
        return try {
            val response = api.getPopularMovies(apiKey, page = page)
            if (response.results.isNotEmpty()) {
                // Sadece ilk sayfada önbelleği temizleyelim
                if (page == 1) {
                    movieDao.clearMovies()
                }
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

    suspend fun getMovieVideoKey(movieId: Int, isTv: Boolean = false): String? {
        return try {
            val response = if (isTv) {
                api.getTvVideos(movieId, apiKey)
            } else {
                api.getMovieVideos(movieId, apiKey)
            }
            response.results.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }?.key
                ?: response.results.firstOrNull { it.site == "YouTube" }?.key
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMovieDetails(movieId: Int, isTv: Boolean = false): MovieDetails? {
        return try {
            if (isTv) api.getTvDetails(movieId, apiKey)
            else api.getMovieDetails(movieId, apiKey)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMovieCredits(movieId: Int, isTv: Boolean = false): CreditsResponse? {
        return try {
            if (isTv) api.getTvCredits(movieId, apiKey)
            else api.getMovieCredits(movieId, apiKey)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSimilarContent(movieId: Int, isTv: Boolean = false): List<Movie> {
        return try {
            val response = if (isTv) api.getSimilarTvShows(movieId, apiKey)
            else api.getSimilarMovies(movieId, apiKey)
            response.results
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchMovies(query: String): Result<List<Movie>> {
        return try {
            val response = api.searchMovies(apiKey, query)
            if (response.results.isNotEmpty()) {
                val uniqueResults = response.results.distinctBy { it.id }
                Result.success(uniqueResults)
            } else {
                Result.failure(Exception("Sonuç bulunamadı."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Movie.toEntity() = MovieEntity(
        id = id,
        title = title,
        overview = overview ?: "",
        posterPath = posterPath,
        releaseDate = date,
        voteAverage = voteAverage
    )

    private fun MovieEntity.toDomain() = Movie(
        id = id,
        movieTitle = title,
        tvName = null,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        firstAirDate = null,
        voteAverage = voteAverage,
        mediaType = "movie"
    )
}
