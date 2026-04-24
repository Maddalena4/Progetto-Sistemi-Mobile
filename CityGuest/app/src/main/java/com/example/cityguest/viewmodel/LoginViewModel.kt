package com.example.cityguest.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/*classe per gestire lo stato della UI (separazione della logica dalla schermata)*/
class LoginViewModel : ViewModel() {
    // Stato dei campi
    var email by mutableStateOf("") /*mutable così si rendono i dati visivibili da Compose (Ricorda: se cambiano cambia anche la UI in modo automatico)*/
    var password by mutableStateOf("")

    // Funzione per il login (si attiva quando l'utente preme il login)
    fun onLoginClick(onSuccess: () -> Unit) {
        if (email.isNotBlank() && password.length >= 6) {
            onSuccess() /*solo se tutto va a buon fine*/
        }
    }
}