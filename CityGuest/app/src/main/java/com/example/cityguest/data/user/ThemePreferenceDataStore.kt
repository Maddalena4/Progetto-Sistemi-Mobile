package com.example.cityguest.data.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Proprietà di estensione per il [Context] che inizializza un'istanza singleton di [DataStore].
 */
val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

/**
 * Enumerazione che definisce i tre possibili stati visivi del tema dell'applicazione.
 */
enum class ThemeMode { LIGHT, DARK, AUTO }

/**
 * Classe delegata alla persistenza e alla lettura delle preferenze sul tema grafico dell'applicazione,
 * */
class ThemePreferenceDataStore(private val context: Context) {

    companion object {
        /**
         * Genera una chiave di preferenza stringa univoca e sicura a partire dall'email dell'utente,
         * sostituendo i caratteri speciali che potrebbero invalidare la chiave nel file XML sottostante.
         */
        private fun themeKey(email: String) =
            stringPreferencesKey("theme_mode_${email.replace("@", "_").replace(".", "_")}")
    }
    /**
     * Mappa il valore stringa letto dal DataStore all'oggetto fortemente tipizzato [ThemeMode].
     * * @param email L'email dell'utente di cui osservare la preferenza.
     * @return Un [Flow] asincrono che emette il [ThemeMode] corrente; restituisce [ThemeMode.AUTO] se non impostato.
     */
    fun observeThemeMode(email: String): Flow<ThemeMode> =
        context.themeDataStore.data.map { prefs ->
            val raw = prefs[themeKey(email)] ?: ThemeMode.AUTO.name
            ThemeMode.valueOf(raw)
        }

    /**
     * Salva in modalità asincrona e thread-safe la preferenza del tema selezionata dall'utente.
     *
     * @param email L'email dell'utente che sta modificando la propria impostazione.
     * @param mode Il nuovo [ThemeMode] da applicare e salvare.
     */
    suspend fun saveThemeMode(email: String, mode: ThemeMode) {
        context.themeDataStore.edit { prefs ->
            prefs[themeKey(email)] = mode.name
        }
    }
}
