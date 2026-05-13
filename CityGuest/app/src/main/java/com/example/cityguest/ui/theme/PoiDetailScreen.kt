package com.example.cityguest.ui.theme

import android.Manifest
import android.content.Intent
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

            results[0] / 1000f

        } else {
            0f
        }
    }

    val calculatedPoints = (distance * 10).toInt() + poi.basePoints

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        AsyncImage(
            model = "https://example.com/image.jpg",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = poi.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = poi.description,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text("📍 Distanza: ${"%.2f".format(distance)} km")

                Text("🏆 Punti attuali: $calculatedPoints")

                Text("📸 Visite: 3")

                Text("⭐ Valutazione: 4/5")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Button(
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
                modifier = Modifier.weight(1f)
            ) {

                Text("AVVIA")
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
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {

                Text("POSTA")
            }
        }
    }
}