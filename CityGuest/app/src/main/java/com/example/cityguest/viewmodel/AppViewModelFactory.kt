package com.example.cityguest.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cityguest.data.user.ThemePreferenceDataStore
import com.example.cityguest.data.user.UserRepository

/**
 * Serve a creare i vari ViewModel dell'applicazione (Login, Registrazione e Profilo).
 * È necessaria perché permette di passare ai ViewModel i componenti esterni di cui hanno bisogno,
 * come il database degli utenti o il sistema di salvataggio delle preferenze (DataStore).
 */
class AppViewModelFactory(
    private val repository: UserRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Controlla quale tipo di ViewModel è stato richiesto e lo crea passandogli i dati corretti
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(repository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(repository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(repository, ThemePreferenceDataStore(context)) as T
            modelClass.isAssignableFrom(SessionViewModel::class.java) ->
                SessionViewModel() as T
            // Se viene richiesto un ViewModel non previsto, lancia un errore
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
