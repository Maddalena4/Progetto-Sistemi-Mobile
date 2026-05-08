package com.example.cityguest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionWrapper(
    content: @Composable () -> Unit
) {
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

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
            Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                Text("CONCEDI PERMESSO")
            }
        }
    }
}