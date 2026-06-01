package com.example.cityguest.data.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

enum class ThemeMode { LIGHT, DARK, AUTO }

class ThemePreferenceDataStore(private val context: Context) {

    companion object {
        private fun themeKey(email: String) =
            stringPreferencesKey("theme_mode_${email.replace("@", "_").replace(".", "_")}")
    }

    fun observeThemeMode(email: String): Flow<ThemeMode> =
        context.themeDataStore.data.map { prefs ->
            val raw = prefs[themeKey(email)] ?: ThemeMode.AUTO.name
            ThemeMode.valueOf(raw)
        }

    suspend fun saveThemeMode(email: String, mode: ThemeMode) {
        context.themeDataStore.edit { prefs ->
            prefs[themeKey(email)] = mode.name
        }
    }
}
