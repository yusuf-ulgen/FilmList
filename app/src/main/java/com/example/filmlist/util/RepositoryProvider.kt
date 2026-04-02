package com.example.filmlist.util

import android.content.Context
import com.example.filmlist.data.local.AppDatabase
import com.example.filmlist.data.local.SessionManager
import com.example.filmlist.data.repository.AuthRepository
import com.example.filmlist.data.repository.MovieRepository
import com.example.filmlist.ui.ViewModelFactory

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

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        return ViewModelFactory(
            provideAuthRepository(context),
            provideMovieRepository(context),
            provideChatRepository()
        )
    }
}
