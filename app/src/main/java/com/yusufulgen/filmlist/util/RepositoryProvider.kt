package com.yusufulgen.filmlist.util

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.yusufulgen.filmlist.data.repository.AuthRepository
import com.yusufulgen.filmlist.data.local.SessionManager
import com.yusufulgen.filmlist.data.local.AppDatabase
import com.yusufulgen.filmlist.data.repository.ChatRepository
import com.yusufulgen.filmlist.data.repository.MovieRepository
import com.yusufulgen.filmlist.data.repository.StatsRepository
import com.yusufulgen.filmlist.ui.ViewModelFactory

object RepositoryProvider {

    fun provideAuthRepository(context: Context): AuthRepository {
        val database = AppDatabase.getDatabase(context)
        val sessionManager = SessionManager(context)
        return AuthRepository(database.userDao(), sessionManager)
    }

    fun provideMovieRepository(context: Context): MovieRepository {
        val database = AppDatabase.getDatabase(context)
        return MovieRepository(database.movieDao())
    }

    fun provideChatRepository(): ChatRepository {
        return ChatRepository()
    }

    private fun provideStatsRepository(context: Context): StatsRepository {
        val database = AppDatabase.getDatabase(context)
        return StatsRepository(database.userDao())
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(
            provideAuthRepository(context),
            provideMovieRepository(context),
            provideChatRepository(),
            provideStatsRepository(context)
        )
    }
}
