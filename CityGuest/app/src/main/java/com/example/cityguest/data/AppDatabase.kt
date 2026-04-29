package com.example.cityguest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// AppDatabase estende RoomDatabase
@Database(entities = [User::class], version = 1) // Questo database contiene la tabella User ed è alla versione 1
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao // Serve per ottenere il DAO e fare operazioni sul database

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            // Evita che più thread creino più database contemporaneamente
            return INSTANCE ?: synchronized(this) {
                //Creazione Database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "cityguest_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}