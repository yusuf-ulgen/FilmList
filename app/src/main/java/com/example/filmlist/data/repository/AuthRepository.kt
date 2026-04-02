package com.example.filmlist.data.repository

import com.example.filmlist.data.local.*
import com.example.filmlist.util.SecurityUtils

class AuthRepository(
    val userDao: UserDao,
    val sessionManager: SessionManager
) {

    suspend fun signUp(email: String, password: String): Boolean {
        val existingUser = userDao.getUserByEmail(email)
        if (existingUser != null) {
            return false
        }
        val hashedPassword = SecurityUtils.hashPassword(password)
        val newUser = User(email = email, passwordHash = hashedPassword)
        val userId = userDao.insertUser(newUser)
        // Auto login after signup
        sessionManager.saveSession(userId, email, true)
        return true
    }

    suspend fun login(email: String, password: String, rememberMe: Boolean): Boolean {
        val user = userDao.getUserByEmail(email) ?: return false

        if (SecurityUtils.verifyPassword(password, user.passwordHash)) {
            sessionManager.saveSession(user.id, user.email, rememberMe)
            return true
        }
        return false
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    fun isLoggedIn() = sessionManager.isLoggedIn
}
