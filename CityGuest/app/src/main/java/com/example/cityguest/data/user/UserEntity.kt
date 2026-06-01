package com.example.cityguest.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Classe di dati (Data Class) che definisce l'entità "users" all'interno del database Room.
 * Ogni istanza di questa classe corrisponde a una riga della tabella associata.
 *
 * @property email Chiave primaria univoca utilizzata per identificare l'utente e per le operazioni di login.
 * @property username Nome identificativo scelto dall'utente all'atto della registrazione.
 * @property password Hash della password dell'utente per scopi di autenticazione locale.
 * @property profileImageUri Percorso URI locale (stringa) dell'immagine del profilo selezionata dall'utente.
 * @property points Punteggio complessivo accumulato dall'utente durante l'interazione con l'applicazione.
 */
@Entity (tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val username: String,
    val password: String,
    val profileImageUri: String? = null,
    val points: Int = 0
)
