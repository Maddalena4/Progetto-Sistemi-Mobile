package com.example.cityguest.ui.theme

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Stato per la posizione della camera
    val defaultPos = LatLng(41.9028, 12.4964)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPos, 12f)
    }

    // Stati per la ricerca e i dialoghi
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    // Usiamo un Box per sovrapporre la barra di ricerca alla mappa
    Box(modifier = Modifier.fillMaxSize()) {

        // MAPPA
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true),
            onMapClick = { latLng ->
                selectedLocation = latLng
                showDialog = true
            },
            contentPadding = PaddingValues(top = 90.dp)
        )

        // BARRA DI RICERCA
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

        // DIALOGO DETTAGLI
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

// Funzione per la ricerca geografica
private fun searchLocation(context: Context, query: String, onResult: (LatLng) -> Unit) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocationName(query, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            onResult(LatLng(address.latitude, address.longitude))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Funzione per aprire l'app esterna di Google Maps
private fun openInGoogleMaps(context: Context, location: LatLng?) {
    location?.let {
        val uri = "geo:${it.latitude},${it.longitude}?q=${it.latitude},${it.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
        intent.setPackage("com.google.android.apps.maps")
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback via browser
            val browserIntent = Intent(Intent.ACTION_VIEW, "http://maps.google.com/?q=${it.latitude},${it.longitude}".toUri())
            context.startActivity(browserIntent)
        }
    }
}