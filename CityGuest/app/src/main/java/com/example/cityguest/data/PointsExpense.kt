package com.example.cityguest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points_expenses")
data class PointsExpense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val cityName: String,
    val pointsSpent: Int,
    val timestamp: Long = System.currentTimeMillis()
)