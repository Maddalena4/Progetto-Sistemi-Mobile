package com.example.cityguest.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterViewModel: ViewModel() {
    // Stato dei campi di testo
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    // Logica di validazione basilare
    fun onRegisterClick(onSuccess: () -> Unit) {
        if (email.contains("@") && password.length >= 6 && password == confirmPassword) {
            // Qui in futuro chiameremo il database
            onSuccess()
        } else {
            // Qui potresti gestire un messaggio di errore
            println("Errore: controlla i dati inseriti")
        }
    }
}