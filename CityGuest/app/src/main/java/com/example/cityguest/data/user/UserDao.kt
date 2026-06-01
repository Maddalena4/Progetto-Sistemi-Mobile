package com.example.cityguest.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cityguest.data.points.PointsEarning
import com.example.cityguest.data.points.PointsExpense
import com.example.cityguest.data.points.UnlockedCity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    fun observeUserByEmail(email: String): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertUnlockedCity(unlockedCity: UnlockedCity)

    @Query("SELECT cityName FROM unlocked_cities WHERE userEmail = :email")
    fun observeUnlockedCities(email: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPointsExpense(expense: PointsExpense)

    @Query("SELECT * FROM points_expenses WHERE userEmail = :email ORDER BY timestamp DESC")
    fun observePointsExpenses(email: String): Flow<List<PointsExpense>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPointsEarning(earning: PointsEarning)

    @Query("SELECT * FROM points_earnings WHERE userEmail = :email ORDER BY timestamp DESC")
    fun observePointsEarnings(email: String): Flow<List<PointsEarning>>
}