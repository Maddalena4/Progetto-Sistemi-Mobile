package com.example.cityguest.data.poi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cityguest.data.poi.PoiStatus
import com.example.cityguest.data.poi.PoiVisit
import kotlinx.coroutines.flow.Flow

@Dao
interface PoiDao {
    @Query("SELECT * FROM poi_status WHERE poiId = :poiId AND userEmail = :userEmail LIMIT 1")
    fun observePoiStatus(poiId: Int, userEmail: String): Flow<PoiStatus?>

    @Query("SELECT * FROM poi_status WHERE poiId = :poiId AND userEmail = :userEmail LIMIT 1")
    suspend fun getPoiStatus(poiId: Int, userEmail: String): PoiStatus?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertOrUpdatePoiStatus(poiStatus: PoiStatus)

    @Query("SELECT * FROM poi_status WHERE userEmail = :userEmail AND isFavorite = 1")
    fun observeFavoritePois(userEmail: String): Flow<List<PoiStatus>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPoiVisit(poiVisit: PoiVisit)

    @Query("SELECT * FROM poi_visits WHERE userEmail = :userEmail ORDER BY timestamp DESC")
    fun observePoiVisits(userEmail: String): Flow<List<PoiVisit>>
}