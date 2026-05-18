package com.example.cityguest.data

import androidx.room.Entity

@Entity(
    tableName = "unlocked_cities",
    primaryKeys = ["userEmail", "cityName"]
)
data class UnlockedCity(
    val userEmail: String,
    val cityName: String
)