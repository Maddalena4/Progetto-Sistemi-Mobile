package com.example.cityguest.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cityguest.data.PoiData
import com.example.cityguest.ui.components.PlaceOfInterest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun CityMapScreen(
    cityName: String,
    cityLocation: LatLng,
    onInfoClick: () -> Unit,
    onPoiClick: (PlaceOfInterest) -> Unit,
    onBack: () -> Unit
) {

    val pointsOfInterest = PoiData.pointsOfInterest

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cityLocation, 14f)
    }

    LaunchedEffect(cityLocation) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(cityLocation, 14f)
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
                    onInfoWindowClick = { onPoiClick(poi) }
                )
            }
        }
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .align(Alignment.TopStart)
                .background(Color.White.copy(alpha = 0.9f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Torna indietro",
                tint = Color.Black
            )
        }

        Surface(
            onClick = onInfoClick,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 56.dp)
                .align(Alignment.BottomStart)
                .size(44.dp),
            shape = RoundedCornerShape(8.dp),
            color = MapsBlue,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Regole",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}