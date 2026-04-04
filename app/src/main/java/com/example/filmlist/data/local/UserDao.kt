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

    @Delete
    suspend fun deleteMediaContent(content: MediaContent)

    @Query("SELECT * FROM media_content WHERE userId = :userId ORDER BY id DESC")
    fun getUserMediaContent(userId: Long): Flow<List<MediaContent>>

    @Query("SELECT * FROM media_content WHERE userId = :userId")
    suspend fun getUserMediaContentSync(userId: Long): List<MediaContent>

    // UserList operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserList(userList: UserList): Long

    @Update
    suspend fun updateUserList(userList: UserList)

    @Delete
    suspend fun deleteUserList(userList: UserList)

    @Query("SELECT * FROM user_lists WHERE userId = :userId ORDER BY orderIndex ASC")
    fun getUserLists(userId: Long): Flow<List<UserList>>

    @Query("SELECT * FROM media_content WHERE listId = :listId ORDER BY id DESC")
    fun getUserMediaContentByList(listId: Long): Flow<List<MediaContent>>

    @Query("SELECT COUNT(*) FROM user_lists WHERE userId = :userId AND name = :name")
    suspend fun countUserListByName(userId: Long, name: String): Int

    @Query("SELECT COUNT(*) FROM media_content WHERE listId = :listId AND title = :title")
    suspend fun countMediaInListByTitle(listId: Long, title: String): Int
}
