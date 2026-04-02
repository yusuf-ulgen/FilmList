package com.example.filmlist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_content")
data class MediaContent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val type: String, // "FILM" or "SHOW"
    val date: String,
    val rating: Int,
    val comment: String?,
    val isSpoiler: Boolean = false
)
