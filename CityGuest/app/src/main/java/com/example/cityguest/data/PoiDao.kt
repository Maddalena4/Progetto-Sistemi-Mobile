package com.example.cityguest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PoiDao {
    @Query("SELECT * FROM poi_status WHERE poiId = :poiId AND userEmail = :userEmail LIMIT 1")
    fun observePoiStatus(poiId: Int, userEmail: String): Flow<PoiStatus?>

    @Query("SELECT * FROM poi_status WHERE poiId = :poiId AND userEmail = :userEmail LIMIT 1")
    suspend fun getPoiStatus(poiId: Int, userEmail: String): PoiStatus?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePoiStatus(poiStatus: PoiStatus)

    @Query("SELECT * FROM poi_status WHERE userEmail = :userEmail AND isFavorite = 1")
    fun observeFavoritePois(userEmail: String): Flow<List<PoiStatus>>
}