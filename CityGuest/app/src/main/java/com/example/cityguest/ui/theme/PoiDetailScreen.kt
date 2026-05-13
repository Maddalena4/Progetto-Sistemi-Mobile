package com.example.cityguest.ui.theme

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.cityguest.navigation.Route
import com.google.android.gms.maps.model.LatLng
import java.io.File

@Composable
fun PoiDetailScreen(
    poi: Route.PoiDetail,
    userLocation: LatLng?,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val distance = remember(userLocation) {
        if (userLocation != null) {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                userLocation.latitude,
                userLocation.longitude,
                poi.lat.toDouble(),
                poi.lng.toDouble(),
                results
            )
            results[0] / 1000 // Trasforma metri in Km
        } else 0.0f
    }

    val calculatedPoints = poi.basePoints + (distance * 10).toInt()

    val photoUri = remember {
        mutableStateOf<Uri?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->

        if (success) {

            println("Foto salvata: ${photoUri.value}")

            // TODO:
            // Upload foto
            // Salvataggio DB
            // Aggiornamento UI

        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        if (isGranted) {

            val file = File(
                context.externalCacheDir,
                "photo_${System.currentTimeMillis()}.jpg"
            )

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            photoUri.value = uri

            cameraLauncher.launch(uri)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Immagine del posto
        AsyncImage(
            model = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=...",
            contentDescription = poi.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = poi.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(18.dp)
            )
            Text(" 0.0 (0 recensioni) • ", style = MaterialTheme.typography.bodyMedium)
            Text(
                "${"%.1f".format(distance)} km da te",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { /* Azione selezione */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.wrapContentWidth()
        ) {
            Text("Select", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Descrizione:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            text = poi.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = {
                    val gmmIntentUri =
                        "google.navigation:q=${poi.lat},${poi.lng}".toUri()

                    val mapIntent = Intent(
                        Intent.ACTION_VIEW,
                        gmmIntentUri
                    )

                    mapIntent.setPackage("com.google.android.apps.maps")

                    context.startActivity(mapIntent)
                },

                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),

                shape = RoundedCornerShape(12.dp),

                border = BorderStroke(2.dp, Color.Black)

            ) {

                Text(
                    "AVVIA",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = {

                    when {

                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED -> {

                            val file = File(
                                context.externalCacheDir,
                                "photo_${System.currentTimeMillis()}.jpg"
                            )

                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )

                            photoUri.value = uri

                            cameraLauncher.launch(uri)
                        }

                        else -> {

                            permissionLauncher.launch(
                                Manifest.permission.CAMERA
                            )
                        }
                    }
                },

                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),

                shape = RoundedCornerShape(12.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),

                border = BorderStroke(2.dp, Color.Black)

            ) {

                Text(
                    "POSTA",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}