package com.example.cityguest.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityguest.data.user.User
import com.example.cityguest.data.user.UserRepository
import com.example.cityguest.utils.hashPassword
import kotlinx.coroutines.launch

/**
 * Gestisce la logica della schermata di login.
 * Controlla se le credenziali inserite sono corrette e permette l'accesso
 * sia tramite email e password classiche, sia tramite l'account Google.
 */
class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    // Viene chiamato quando l'utente preme sul pulsante di login classico
    fun onLoginClick(onSuccess: (User) -> Unit) {

        viewModelScope.launch {

            val user = repository.getUser(email)

            when {
                // Se l'email non è nel database, mostra l'errore
                user == null -> errorMessage = "Profilo non esistente"

                // Se la password calcolata (hash) non corrisponde a quella salvata, blocca l'accesso
                user.password != hashPassword(password) ->
                    errorMessage = "Password errata"

                else -> {
                    // Se è tutto corretto, pulisce gli errori e fa entrare l'utente
                    errorMessage = null
                    onSuccess(user)
                }
            }
        }
    }

    // Gestisce l'accesso con Google: se l'utente esiste già lo fa entrare, altrimenti lo registra sul momento
    fun onGoogleLoginSuccess(email: String, username: String, onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            val existingUser = repository.getUser(email)
            if (existingUser != null) {
                onSuccess(existingUser)
            } else {
                // Crea un nuovo profilo usando una password di sistema fissa per gli utenti Google
                val newUser = User(
                    email = email,
                    username = username,
                    password = "GOOGLE_OAUTH_USER"
                )
                repository.register(newUser)
                onSuccess(newUser)
            }
        }
    }
}