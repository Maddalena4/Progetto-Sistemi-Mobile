package com.example.cityguest.data

import androidx.room.Entity

@Entity(
    tableName = "poi_status",
    primaryKeys = ["userEmail", "poiId"]
)
data class PoiStatus(
    val userEmail: String,
    val poiId: Int,
    val photoUri: String?,
    val visits: Int = 0,
    val stars: Int = 0
)