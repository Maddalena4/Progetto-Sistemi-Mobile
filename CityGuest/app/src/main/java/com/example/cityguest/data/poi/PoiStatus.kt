package com.example.cityguest.data.poi

import androidx.room.Entity

/**
 * Rappresenta lo stato e le preferenze correnti di un utente per un determinato Punto di Interesse (POI).
 *
 * Questa entità utilizza una chiave primaria composta per garantire che esista una e una sola riga
 * per ogni specifica combinazione utente-luogo all'interno del database.
 *
 * @property userEmail L'indirizzo email dell'utente associato a questo stato.
 * @property poiId L'identificativo univoco del Punto di Interesse.
 * @property poiName Il nome leggibile del Punto di Interesse.
 * @property photoUri L'URI locale di un'eventuale foto scattata dall'utente per questo luogo, o `null` se assente.
 * @property visits Il contatore totale delle visite effettuate dall'utente in questo POI.
 * @property stars La valutazione in stelle assegnata dall'utente al luogo.
 * @property isFavorite Indica se l'utente ha salvato questo POI nella sua lista dei preferiti.
 */
@Entity(
    tableName = "poi_status",
    primaryKeys = ["userEmail", "poiId"]
)
data class PoiStatus(
    val userEmail: String,
    val poiId: String,
    val poiName: String = "",
    val photoUri: String?,
    val visits: Int = 0,
    val stars: Int = 0,
    val isFavorite: Boolean = false
)