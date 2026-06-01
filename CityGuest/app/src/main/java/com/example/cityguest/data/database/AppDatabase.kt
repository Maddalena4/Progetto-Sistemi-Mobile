package com.example.cityguest.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cityguest.data.poi.PoiDao
import com.example.cityguest.data.poi.PoiStatus
import com.example.cityguest.data.poi.PoiVisit
import com.example.cityguest.data.points.PointsEarning
import com.example.cityguest.data.points.PointsExpense
import com.example.cityguest.data.points.UnlockedCity
import com.example.cityguest.data.user.User
import com.example.cityguest.data.user.UserDao

@Database(
    entities = [
        User::class,
        PoiStatus::class,
        PoiVisit::class,
        UnlockedCity::class,
        PointsExpense::class,
        PointsEarning::class
    ],
    version = 6
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun poiDao(): PoiDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "cityguest_db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}