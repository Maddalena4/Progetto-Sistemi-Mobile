package com.example.cityguest.ui.theme

import androidx.core.net.toUri
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton


@Composable
fun MapScreen() {
    val context = LocalContext.current
    // Posizione di default (es. Roma) se non c'è ultima posizione nota
    val defaultPos = LatLng(41.9028, 12.4964)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPos, 12f)
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    // Configurazione proprietà mappa
    val uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)) }
    val properties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapClick = { latLng ->
            selectedLocation = latLng
            showDialog = true
        }
    )

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

private fun openInGoogleMaps(context: Context, location: LatLng?) {
    location?.let {
        val uri = "geo:${it.latitude},${it.longitude}?q=${it.latitude},${it.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
        intent.setPackage("com.google.android.apps.maps")
        context.startActivity(intent)
    }
}