package com.example.cityguest.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityguest.data.User
import com.example.cityguest.data.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository): ViewModel() {
    // Stato dei campi di testo
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    // Logica di validazione basilare
    fun onRegisterClick(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (repository.isEmailRegistered(email)) {
                errorMessage = "Questo profilo esiste già!"
            } else {
                // Crea il profilo
                repository.register(User(email, username, password))
                errorMessage = null
                onSuccess()
            }
        }
    }
}