package com.example.cityguest.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cityguest.ui.components.PlaceOfInterest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun CityMapScreen(
    cityName: String,
    onInfoClick: () -> Unit,
    onPoiClick: (PlaceOfInterest) -> Unit
) {
    val forliLocation = LatLng(44.2221, 12.0390)

    val pointsOfInterest = listOf(
        PlaceOfInterest("1", "Duomo di Forlì", "Un'abbazia storica nel cuore della città.", LatLng(44.2221, 12.0390), 100, ""),
        PlaceOfInterest("2", "San Mercuriale", "Famosa abbazia con un imponente campanile.", LatLng(44.2228, 12.0408), 120, "")
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(forliLocation, 15f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)
        ) {
            pointsOfInterest.forEach { poi ->
                Marker(
                    state = MarkerState(position = poi.location),
                    title = poi.name,
                    snippet = "Clicca qui per dettagli",
                    onInfoWindowClick = {
                        onPoiClick(poi)
                    }
                )
            }
        }

        Surface(
            onClick = onInfoClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .size(44.dp),
            shape = RoundedCornerShape(8.dp),
            color = PureWhite,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Regole",
                    tint = MapsBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}