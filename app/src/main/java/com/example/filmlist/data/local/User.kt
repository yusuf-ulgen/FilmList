package com.example.filmlist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val nickname: String? = null,
    val profilePictureUri: String? = null,
    val birthDate: String? = null,
    val gender: String? = null
)
