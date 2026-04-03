package com.example.filmlist.data.repository

import com.example.filmlist.data.local.MediaContent
import com.example.filmlist.data.local.UserDao
import kotlinx.coroutines.flow.firstOrNull

class StatsRepository(private val userDao: UserDao) {

    data class UserStats(
        val totalWatched: Int,
        val movieCount: Int,
        val showCount: Int,
        val favoriteGenre: String,
        val showHabit: String // "Sitcom" or "Long-form / Epic"
    )

    suspend fun getUserStats(userId: Long): UserStats {
        val contents = userDao.getUserMediaContentSync(userId)
        val movies = contents.filter { it.type == "FILM" }
        val shows = contents.filter { it.type == "SHOW" }

        val genres = contents.flatMap { it.genre?.split(",") ?: emptyList() }
            .groupingBy { it.trim() }
            .eachCount()
        val favoriteGenre = genres.maxByOrNull { it.value }?.key ?: "Belirlenemedi"

        // Analyze show habits
        // Sitcoms usually have many episodes but shorter runtimes (~20-30m)
        // For now, we mock the logic based on the count or simplified analysis
        val sitcomCount = shows.count { it.runtime?.contains("20") == true || it.runtime?.contains("30") == true }
        val longFormCount = shows.size - sitcomCount
        val showHabit = if (shows.isEmpty()) "Henüz veri yok" 
                        else if (sitcomCount > longFormCount) "Sitcom / Çerezlik" 
                        else "Uzun Soluklu / Epik"

        return UserStats(
            totalWatched = contents.size,
            movieCount = movies.size,
            showCount = shows.size,
            favoriteGenre = favoriteGenre,
            showHabit = showHabit
        )
    }
}
