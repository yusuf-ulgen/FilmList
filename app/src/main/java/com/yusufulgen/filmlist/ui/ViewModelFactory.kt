package com.yusufulgen.filmlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yusufulgen.filmlist.data.repository.AuthRepository
import com.yusufulgen.filmlist.data.repository.ChatRepository
import com.yusufulgen.filmlist.data.repository.MovieRepository
import com.yusufulgen.filmlist.ui.auth.LoginViewModel
import com.yusufulgen.filmlist.ui.auth.SignUpViewModel
import com.yusufulgen.filmlist.ui.home.HomeViewModel
import com.yusufulgen.filmlist.ui.chat.ChatViewModel
import com.yusufulgen.filmlist.ui.categories.CategoriesViewModel
import com.yusufulgen.filmlist.ui.profile.ProfilingViewModel
import com.yusufulgen.filmlist.ui.add.AddContentViewModel
import com.yusufulgen.filmlist.ui.users.UserListViewModel
import com.yusufulgen.filmlist.data.repository.StatsRepository
import com.yusufulgen.filmlist.ui.profile.ProfileViewModel

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
                HomeViewModel(movieRepository!!, chatRepository!!, authRepository.userDao, authRepository.sessionManager) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(chatRepository!!, authRepository) as T
            }
            modelClass.isAssignableFrom(CategoriesViewModel::class.java) -> {
                CategoriesViewModel(authRepository.sessionManager) as T
            }
            modelClass.isAssignableFrom(ProfilingViewModel::class.java) -> {
                ProfilingViewModel(authRepository.userDao, authRepository.sessionManager) as T
            }
            modelClass.isAssignableFrom(AddContentViewModel::class.java) -> {
                AddContentViewModel(authRepository.userDao, authRepository.sessionManager, movieRepository!!) as T
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
