package com.example.cityguest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*

/**
 * Isola la logica di richiesta dei permessi di Android dall'interfaccia utente delle schermate principali.
 * Utilizza la libreria Google Accompanist per interfacciarsi con il sistema di permessi a runtime.
 *
 * @param content Il blocco composable (Slot API) che verrà renderizzato esclusivamente se il permesso è concesso.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionWrapper(
    content: @Composable () -> Unit
) {
    // Inizializza lo stato del permesso per la geolocalizzazione accurata (FINE_LOCATION)
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    //controlla se l'utente ha già concesso il permesso
    if (locationPermissionState.status.isGranted) {
        content()
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CityQuest ha bisogno della tua posizione per farti giocare e mostrarti i punti sulla mappa.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Bottone che innesca il prompt di sistema nativo Android per la richiesta del permesso
            Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                Text("CONCEDI PERMESSO")
            }
        }
    }
}