package com.example.cityguest.data.points

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Mappa la tabella "points_earnings", utilizzata per tracciare lo storico
 * dei punti accumulati dall'utente visitando i Punti di Interesse (POI).
 *
 * @property id Identificativo univoco auto-generato per la riga dello storico transazioni.
 * @property userEmail Riferimento logico (chiave esterna concettuale) all'utente che ha ottenuto i punti.
 * @property poiName Nome del Punto di Interesse che ha generato il premio in punti.
 * @property pointsEarned Quantità di punti accreditati per l'azione.
 * @property timestamp Rappresentazione temporale in millisecondi del momento dell'accredito (default all'ora corrente del sistema).
 */
@Entity(tableName = "points_earnings")
data class PointsEarning(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val poiName: String,
    val pointsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)