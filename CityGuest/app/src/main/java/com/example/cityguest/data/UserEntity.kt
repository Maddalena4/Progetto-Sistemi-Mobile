package com.example.cityguest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//classe per tabella utenti
@Entity (tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String, //l'email è la chiave univoca
    val username: String,
    val password: String
)
