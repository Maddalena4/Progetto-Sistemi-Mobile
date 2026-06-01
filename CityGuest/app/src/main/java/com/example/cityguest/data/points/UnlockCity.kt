package com.example.cityguest.data.points

import androidx.room.Entity

/**
 * Mappa la tabella di associazione "unlocked_cities".
 * Implementa una chiave primaria composta per modellare una relazione logica molti-a-molti
 * che traccia quali città siano state rese accessibili a quale utente.
 *
 * @property userEmail Parte della chiave primaria composta; identifica l'utente che detiene il diritto di accesso.
 * @property cityName Parte della chiave primaria composta; definisce il nome della città sbloccata dall'utente.
 */
@Entity(
    tableName = "unlocked_cities",
    primaryKeys = ["userEmail", "cityName"]
)
data class UnlockedCity(
    val userEmail: String,
    val cityName: String
)