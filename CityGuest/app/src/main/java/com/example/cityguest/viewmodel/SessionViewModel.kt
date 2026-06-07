package com.example.cityguest.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel responsabile della gestione della sessione utente attiva nell'applicazione.
 *
 * Espone l'email dell'utente correntemente autenticato come [StateFlow], garantendo
 * che tutti i componenti dell'UI che la osservano vengano aggiornati reattivamente
 * ad ogni cambio di stato (login o logout).
 *
 * Sopravvive ai cambi di configurazione (es. rotazione schermo) grazie al ciclo di vita
 * del [ViewModel], evitando la perdita dello stato di sessione.
 */
class SessionViewModel : ViewModel() {

    /**
     * Stato interno mutabile dell'email utente, accessibile solo all'interno del ViewModel.
     * Inizializzato con una stringa vuota che rappresenta l'assenza di sessione attiva.
     */
    private val _userEmail = MutableStateFlow("")
    /**
     * Stato pubblico e immutabile dell'email utente, esposto alla UI tramite [StateFlow].
     * Una stringa vuota indica che nessun utente è attualmente autenticato.
     */
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    /**
     * Registra l'accesso di un utente salvando la sua email nello stato di sessione.
     *
     * @param email L'indirizzo email dell'utente che ha effettuato il login con successo.
     */
    fun login(email: String) {
        _userEmail.value = email
    }

    /**
     * Termina la sessione corrente azzerando l'email dell'utente autenticato.
     * Dopo questa chiamata, [userEmail] emette una stringa vuota.
     */
    fun logout() {
        _userEmail.value = ""
    }
}