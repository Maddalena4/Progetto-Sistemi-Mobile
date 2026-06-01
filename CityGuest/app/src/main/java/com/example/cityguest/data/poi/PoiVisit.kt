package com.example.cityguest.data.poi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poi_visits")
data class PoiVisit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val poiId: Int,
    val poiName: String,
    val distanceKm: Float,
    val timestamp: Long = System.currentTimeMillis()
)