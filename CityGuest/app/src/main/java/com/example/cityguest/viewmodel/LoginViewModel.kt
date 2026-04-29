package com.example.cityguest.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityguest.data.UserRepository
import kotlinx.coroutines.launch

/*classe per gestire lo stato della UI (separazione della logica dalla schermata)*/
class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    // Stato dei campi
    var email by mutableStateOf("") /*mutable così si rendono i dati visivibili da Compose (Ricorda: se cambiano cambia anche la UI in modo automatico)*/
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    // Funzione per il login (si attiva quando l'utente preme il login)
    fun onLoginClick(onSuccess: () -> Unit) {

        viewModelScope.launch {
            // Controllo account esistente
            val user = repository.getUser(email)
            when {
                user == null -> errorMessage = "Profilo non esistente"
                user.password != password -> errorMessage = "Password errata"
                else -> {
                    errorMessage = null
                    onSuccess()
                }
            }
        }
    }
}