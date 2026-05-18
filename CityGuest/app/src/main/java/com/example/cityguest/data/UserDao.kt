package com.example.cityguest.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    fun observeUserByEmail(email: String): kotlinx.coroutines.flow.Flow<User?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUnlockedCity(unlockedCity: UnlockedCity)

    @Query("SELECT cityName FROM unlocked_cities WHERE userEmail = :email")
    fun observeUnlockedCities(email: String): Flow<List<String>>
}