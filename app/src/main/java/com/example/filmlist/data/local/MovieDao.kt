package com.example.filmlist.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM cached_movies")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM cached_movies")
    suspend fun getAllMoviesBySync(): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM cached_movies")
    suspend fun clearMovies()
}
