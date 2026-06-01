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

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    fun onLoginClick(onSuccess: (User) -> Unit) {

        viewModelScope.launch {

            val user = repository.getUser(email)

            when {
                user == null -> errorMessage = "Profilo non esistente"

                user.password != hashPassword(password) ->
                    errorMessage = "Password errata"

                else -> {
                    errorMessage = null
                    onSuccess(user)
                }
            }
        }
    }

    fun onGoogleLoginSuccess(email: String, username: String, onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            val existingUser = repository.getUser(email)
            if (existingUser != null) {
                onSuccess(existingUser)
            } else {
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