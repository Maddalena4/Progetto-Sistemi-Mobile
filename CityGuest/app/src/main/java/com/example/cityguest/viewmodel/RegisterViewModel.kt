package com.example.cityguest.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityguest.data.user.User
import com.example.cityguest.data.user.UserRepository
import com.example.cityguest.utils.hashPassword
import com.example.cityguest.utils.isValidEmail
import kotlinx.coroutines.launch

/**
 * Gestisce la logica della schermata di registrazione.
 * Tiene traccia di quello che scrive l'utente e controlla che i dati siano validi
 * prima di creare il nuovo account.
 */
class RegisterViewModel(private val repository: UserRepository): ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    fun onRegisterClick(onSuccess: () -> Unit) {
        viewModelScope.launch {

            // Controlla che l'email sia scritta bene
            if (!isValidEmail(email)) {
                errorMessage = "Email non valida"
                return@launch
            }
            //Controlla che la password sia almeno lunga 6 cifre
            if (password.length < 6) {
                errorMessage = "La password deve essere di almeno 6 caratteri"
                return@launch
            }

            // Controlla che le due password inserite siano uguali
            if (password != confirmPassword) {
                errorMessage = "Le password non coincidono"
                return@launch
            }

            // Controlla se esiste già un account con questa email, altrimenti lo crea
            if (repository.isEmailRegistered(email)) {
                errorMessage = "Questo profilo esiste già!"
            } else {
                val hashedPassword = hashPassword(password)
                repository.register(User(email, username, hashedPassword))
                errorMessage = null
                onSuccess()
            }
        }
    }
}