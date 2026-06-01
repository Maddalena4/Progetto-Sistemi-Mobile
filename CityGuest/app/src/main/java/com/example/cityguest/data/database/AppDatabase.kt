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

/**
 * Classe astratta che funge da punto di accesso principale per il database locale dell'applicazione,
 * basata sulla libreria di persistenza Jetpack Room.
 * * L'annotazione `@Database` definisce la configurazione del database:
 * - [entities]: Elenco di tutte le classi data class (tabelle) gestite da Room.
 * - [version]: Versione corrente dello schema del database (utilizzata per gestire le migrazioni).
 */
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
    /**
     * Fornisce l'accesso ai metodi di manipolazione dati (Query, Insert, Update, Delete)
     * definiti nell'interfaccia [UserDao] relativi agli utenti.
     */
    abstract fun userDao(): UserDao
    /**
     * Fornisce l'accesso ai metodi di manipolazione dati definiti nell'interfaccia [PoiDao]
     * relativi ai Punti di Interesse e alle dinamiche dei punteggi.
     */
    abstract fun poiDao(): PoiDao

    companion object {
        /**
         * La marcatura `@Volatile` garantisce che il valore di [INSTANCE] sia sempre aggiornato
         * e visibile immediatamente a tutti i thread di esecuzione.
         */
        @Volatile private var INSTANCE: AppDatabase? = null
        /**
         * Restituisce l'istanza unica del database applicando il Pattern Singleton.
         * Se il database non è ancora stato inizializzato, viene creato in modo thread-safe.
         *
         * @param context Il contesto dell'applicazione necessario per inizializzare il database Builder.
         * @return L'istanza singleton di [AppDatabase].
         */
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