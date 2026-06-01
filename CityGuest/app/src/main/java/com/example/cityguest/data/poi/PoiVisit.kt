package com.example.cityguest.data.poi

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta il log di una singola visita effettuata da un utente presso un Punto di Interesse.
 *
 * @property id L'identificativo univoco della visita, generato automaticamente da Room.
 * @property userEmail L'indirizzo email dell'utente che ha registrato la visita.
 * @property poiId L'identificativo del Punto di Interesse visitato.
 * @property poiName Il nome del Punto di Interesse al momento della registrazione.
 * @property distanceKm La distanza calcolata (in chilometri) tra la posizione dell'utente e il POI al momento del check-in.
 * @property timestamp La marca temporale della visita, espressa in millisecondi (Unix epoch).
 */
@Entity(tableName = "poi_visits")
data class PoiVisit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val poiId: Int,
    val poiName: String,
    val distanceKm: Float,
    val timestamp: Long = System.currentTimeMillis()
)