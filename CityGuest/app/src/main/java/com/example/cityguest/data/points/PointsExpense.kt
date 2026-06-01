package com.example.cityguest.data.points

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Mappa la tabella "points_expenses", finalizzata alla memorizzazione
 * persistente delle transazioni di "spesa" dei punti effettuizzate dall'utente (es. sblocco città).
 *
 * @property id Identificativo univoco auto-generato della transazione di spesa.
 * @property userEmail Riferimento logico all'utente che ha effettuato la spesa.
 * @property cityName Nome della città sbloccata utilizzando il saldo punti.
 * @property pointsSpent Quantità di punti scalati dal portafoglio virtuale dell'utente.
 * @property timestamp Rappresentazione temporale in millisecondi del momento della transazione.
 */
@Entity(tableName = "points_expenses")
data class PointsExpense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val cityName: String,
    val pointsSpent: Int,
    val timestamp: Long = System.currentTimeMillis()
)