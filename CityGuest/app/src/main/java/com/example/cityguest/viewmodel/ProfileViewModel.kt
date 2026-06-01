package com.example.cityguest.viewmodel

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityguest.data.user.ThemeMode
import com.example.cityguest.data.user.ThemePreferenceDataStore
import com.example.cityguest.data.user.UserRepository
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.example.cityguest.utils.hashPassword

/**
 * Gestisce i dati del profilo dell'utente.
 * Serve a caricare le informazioni salvate (come l'immagine o il tema grafico preferito)
 * e ad aggiornare i dati se l'utente decide di modificarli.
 */
class ProfileViewModel(
    private val repository: UserRepository,
    private val themeDataStore: ThemePreferenceDataStore
) : ViewModel() {

    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var profileImageUri by mutableStateOf<Uri?>(null)
    var newPassword by mutableStateOf("")

    private var _themeMode = mutableStateOf(ThemeMode.AUTO)
    val themeMode: ThemeMode get() = _themeMode.value

    // Carica i dati dell'utente dal database e osserva se cambia la preferenza sul tema (Chiaro/Scuro)
    fun initUser(userEmail: String, userName: String) {
        email = userEmail
        username = userName
        profileImageUri = null

        viewModelScope.launch {
            val user = repository.getUser(userEmail)
            user?.profileImageUri?.let {
                profileImageUri = it.toUri()
            }

            themeDataStore.observeThemeMode(userEmail).collect { mode ->
                _themeMode.value = mode
            }
        }
    }

    // Salva nelle preferenze dell'app la scelta del tema (Chiaro, Scuro o Automatico)
    fun saveThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeDataStore.saveThemeMode(email, mode)
        }
    }

    // Salva le modifiche fatte al profilo (nome, foto e password se è stata cambiata)
    fun saveProfileChanges(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val currentUser = repository.getUser(email)
            if (currentUser != null) {
                // Se l'utente ha scritto una nuova password la cripta, altrimenti tiene quella vecchia
                val passwordToSave = if (newPassword.isNotEmpty()) {
                    hashPassword(newPassword)
                } else {
                    currentUser.password
                }
                val updatedUser = currentUser.copy(
                    username = username,
                    password = passwordToSave,
                    profileImageUri = profileImageUri?.toString()
                )
                repository.updateUser(updatedUser)
                newPassword = ""
                onSuccess(username)
            }
        }
    }
}
