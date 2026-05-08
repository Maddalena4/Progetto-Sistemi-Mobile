package com.example.cityguest.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun CityMapScreen(
    cityName: String,
    onInfoClick: () -> Unit
) {
    val forliLocation = LatLng(44.2227, 12.0409)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(forliLocation, 15f)
    }

    val pointsOfInterest = listOf(
        LatLng(44.2221, 12.0390),
        LatLng(44.2250, 12.0450)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)
        ) {
            pointsOfInterest.forEach { poi ->
                Marker(
                    state = MarkerState(position = poi),
                    title = "Luogo da Visitare",
                    snippet = "Raggiungi questo punto per guadagnare punti!"
                )
            }
        }

        FloatingActionButton(
            onClick = onInfoClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Info, contentDescription = "Regole")
        }
    }
}