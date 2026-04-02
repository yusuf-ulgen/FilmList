package com.example.filmlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.filmlist.data.repository.AuthRepository
import com.example.filmlist.ui.auth.LoginViewModel
import com.example.filmlist.ui.auth.SignUpViewModel
import com.example.filmlist.ui.categories.CategoriesViewModel
import com.example.filmlist.ui.profile.ProfilingViewModel
import com.example.filmlist.ui.add.AddContentViewModel
import com.example.filmlist.ui.users.UserListViewModel

class ViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CategoriesViewModel::class.java) -> {
                CategoriesViewModel() as T
            }
            modelClass.isAssignableFrom(ProfilingViewModel::class.java) -> {
                ProfilingViewModel(repository.userDao, repository.sessionManager) as T
            }
            modelClass.isAssignableFrom(AddContentViewModel::class.java) -> {
                AddContentViewModel(repository.userDao, repository.sessionManager) as T
            }
            modelClass.isAssignableFrom(UserListViewModel::class.java) -> {
                UserListViewModel(repository.userDao, repository.sessionManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
