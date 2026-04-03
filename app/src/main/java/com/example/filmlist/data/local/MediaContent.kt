package com.example.filmlist.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "media_content",
    foreignKeys = [
        ForeignKey(
            entity = UserList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaContent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val listId: Long, // Linked to UserList
    val title: String,
    val type: String, // "FILM" or "SHOW"
    val date: String,
    val rating: Int,
    val comment: String? = null,
    val posterPath: String? = null,
    val genre: String? = null,
    val runtime: String? = null,
    val cast: String? = null,
    val tmdbId: Int? = null // For data syncing/fetching
)
