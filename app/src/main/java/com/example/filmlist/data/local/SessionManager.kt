package com.example.filmlist.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_sessions")

class SessionManager(private val context: Context) {

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = longPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
        private val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
        private val SELECTED_FILM_CATEGORIES = stringSetPreferencesKey("selected_film_categories")
        private val SELECTED_DIZI_CATEGORIES = stringSetPreferencesKey("selected_dizi_categories")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val userId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    suspend fun saveSession(userId: Long, email: String, rememberMe: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
            preferences[REMEMBER_ME] = rememberMe
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences[USER_ID] = -1L
        }
    }

    val profileImageUri: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PROFILE_IMAGE_URI]
    }

    suspend fun saveProfileImage(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_IMAGE_URI] = uri
        }
    }

    val selectedFilmCategories: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_FILM_CATEGORIES] ?: emptySet()
    }

    val selectedDiziCategories: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_DIZI_CATEGORIES] ?: emptySet()
    }

    suspend fun saveCategories(filmCategories: Set<String>, diziCategories: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_FILM_CATEGORIES] = filmCategories
            preferences[SELECTED_DIZI_CATEGORIES] = diziCategories
        }
    }
}
