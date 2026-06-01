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

class RegisterViewModel(private val repository: UserRepository): ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    fun onRegisterClick(onSuccess: () -> Unit) {
        viewModelScope.launch {

            if (!isValidEmail(email)) {
                errorMessage = "Email non valida"
                return@launch
            }

            if (password != confirmPassword) {
                errorMessage = "Le password non coincidono"
                return@launch
            }

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