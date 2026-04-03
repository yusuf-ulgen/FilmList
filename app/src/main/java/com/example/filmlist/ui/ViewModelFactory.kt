package com.example.filmlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.filmlist.data.repository.AuthRepository
import com.example.filmlist.data.repository.ChatRepository
import com.example.filmlist.data.repository.MovieRepository
import com.example.filmlist.ui.auth.LoginViewModel
import com.example.filmlist.ui.auth.SignUpViewModel
import com.example.filmlist.ui.home.HomeViewModel
import com.example.filmlist.ui.chat.ChatViewModel
import com.example.filmlist.ui.categories.CategoriesViewModel
import com.example.filmlist.ui.profile.ProfilingViewModel
import com.example.filmlist.ui.add.AddContentViewModel
import com.example.filmlist.ui.users.UserListViewModel
import com.example.filmlist.data.repository.StatsRepository
import com.example.filmlist.ui.profile.ProfileViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val movieRepository: MovieRepository? = null,
    private val chatRepository: ChatRepository? = null,
    private val statsRepository: StatsRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(movieRepository!!) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(chatRepository!!, authRepository) as T
            }
            modelClass.isAssignableFrom(CategoriesViewModel::class.java) -> {
                CategoriesViewModel() as T
            }
            modelClass.isAssignableFrom(ProfilingViewModel::class.java) -> {
                ProfilingViewModel(authRepository.userDao, authRepository.sessionManager) as T
            }
            modelClass.isAssignableFrom(AddContentViewModel::class.java) -> {
                AddContentViewModel(authRepository.userDao, authRepository.sessionManager) as T
            }
            modelClass.isAssignableFrom(UserListViewModel::class.java) -> {
                UserListViewModel(authRepository.userDao, authRepository.sessionManager) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(authRepository.userDao, authRepository.sessionManager, statsRepository!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
