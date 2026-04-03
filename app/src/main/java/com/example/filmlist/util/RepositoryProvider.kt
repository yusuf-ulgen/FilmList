package com.example.filmlist.util

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
