package com.example.cityguest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points_earnings")
data class PointsEarning(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val poiName: String,
    val pointsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)