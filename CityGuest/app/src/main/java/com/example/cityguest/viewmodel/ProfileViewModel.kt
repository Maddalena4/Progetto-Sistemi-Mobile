package com.example.cityguest.viewmodel

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityguest.data.UserRepository
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var profileImageUri by mutableStateOf<Uri?>(null)
    var newPassword by mutableStateOf("")

    fun initUser(userEmail: String, userName: String) {
        email = userEmail
        username = userName

        viewModelScope.launch {
            val user = repository.getUser(userEmail)
            // Se esiste un URI salvato, lo riconvertiamo da String a Uri
            user?.profileImageUri?.let {
                profileImageUri = it.toUri()
            }
        }
    }

    fun saveProfileChanges(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            // Recuperiamo l'utente attuale per non perdere la password se non viene cambiata
            val currentUser = repository.getUser(email)
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    username = username,
                    password = newPassword.ifEmpty { currentUser.password },
                    profileImageUri = profileImageUri?.toString()
                )
                repository.updateUser(updatedUser)
                onSuccess(username) // Passiamo il nuovo username per aggiornare la UI
            }
        }
    }

    fun clearData() {
        username = ""
        email = ""
        profileImageUri = null
        newPassword = ""
    }
}