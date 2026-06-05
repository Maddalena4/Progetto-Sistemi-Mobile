package com.example.cityguest.ui.screens.map

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.util.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Schermata principale della mappa interattiva del gioco.
 * Sfrutta la libreria Google Maps Compose per incapsulare il ciclo di vita del MapView.
 * Gestisce in maniera reattiva lo stato della telecamera e implementa l'Overlay UI per la barra di ricerca.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    // LocalContext fornisce il riferimento al contesto dell'Activity corrente all'interno del grafo Compose
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    val isLocationEnabled = locationPermission.status.isGranted


    // Stato per la posizione della camera è memorizzato a runtime
    val defaultPos = LatLng(41.9028, 12.4964)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPos, 12f)
    }

    // Stati per la ricerca e i dialoghi
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = isLocationEnabled),
            uiSettings = MapUiSettings(myLocationButtonEnabled = isLocationEnabled),
            onMapClick = { latLng ->
                selectedLocation = latLng
                showDialog = true
            },
            contentPadding = PaddingValues(top = 90.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.medium
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cerca una città o indirizzo...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotEmpty()) {
                            searchLocation(context, searchQuery) { latLng ->
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(latLng, 13f)
                                    )
                                }
                            }
                        }
                    }
                )
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Dettagli Luogo") },
                text = { Text("Vuoi visualizzare questo punto su Google Maps per maggiori informazioni?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        openInGoogleMaps(context, selectedLocation)
                    }) { Text("Sì, apri Maps") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Annulla") }
                }
            )
        }
    }
}

/**
 * Esegue la conversione da indirizzo a coordinate geografiche.
 * Sfrutta la classe nativa [Geocoder] del sistema operativo Android.
 *
 * @param context Contesto operativo per l'interrogazione dei servizi di geolocalizzazione di sistema.
 * @param query Stringa testuale del luogo inserita dall'utente (es. "Colosseo").
 * @param onResult Funzione di callback (State Hoisting) che restituisce l'oggetto [LatLng] individuato.
 */
private fun searchLocation(context: Context, query: String, onResult: (LatLng) -> Unit) {
    val geocoder = Geocoder(context, Locale.getDefault())
    @Suppress("DEPRECATION")
    try {
        // Richiesta sincrona al provider di mappe del sistema Android
        val addresses = geocoder.getFromLocationName(query, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            onResult(LatLng(address.latitude, address.longitude)) // Notifica le coordinate estratte
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Implementa un Intent Implicito per delegare la navigazione stradale all'applicazione di Google Maps.
 *
 * @param context Contesto necessario per l'avvio della nuova Activity.
 * @param location Coordinate di destinazione verso cui puntare il navigatore.
 */
private fun openInGoogleMaps(context: Context, location: LatLng?) {
    location?.let {
        // Strutturazione dell'URI geografico secondo lo standard Android "geo:lat,lng?q=lat,lng"
        val uri = "geo:${it.latitude},${it.longitude}?q=${it.latitude},${it.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
        intent.setPackage("com.google.android.apps.maps")
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            val browserIntent = Intent(Intent.ACTION_VIEW, "http://maps.google.com/?q=${it.latitude},${it.longitude}".toUri())
            context.startActivity(browserIntent)

        }
    }
}