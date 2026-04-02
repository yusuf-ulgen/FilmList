package com.example.filmlist.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Long): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // Media Content operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaContent(content: MediaContent): Long

    @Query("SELECT * FROM media_content WHERE userId = :userId ORDER BY id DESC")
    fun getUserMediaContent(userId: Long): Flow<List<MediaContent>>
}
