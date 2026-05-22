package com.example.cityguest.ui.theme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cityguest.data.PoiDao
import com.example.cityguest.data.PoiStatus
import com.example.cityguest.navigation.Route
import com.example.cityguest.utils.saveImageToInternalStorage
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PoiDetailScreen(
    poi: Route.PoiDetail,
    userLocation: LatLng?,
    poiDao: PoiDao,
    navController: NavController,
    isJustUploaded: Boolean,
    currentUserEmail: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val poiStatusState = poiDao.observePoiStatus(poi.id, currentUserEmail).collectAsState(initial = null)
    val poiStatus = poiStatusState.value

    val visits = poiStatus?.visits ?: 0
    val savedStars = poiStatus?.stars ?: 0
    val savedPhotoUri = poiStatus?.photoUri

    val isFavorite = poiStatus?.isFavorite ?: false
    var selectedStars by remember(savedStars) { mutableIntStateOf(savedStars) }

    val distance = remember(userLocation) {
        if (userLocation != null) {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                userLocation.latitude.toDouble(), userLocation.longitude.toDouble(),
                poi.lat.toDouble(), poi.lng.toDouble(), results
            )
            results[0] / 1000f
        } else 0f
    }

    val calculatedPoints = poi.basePoints + (distance * 25).toInt()
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPhotoUri.value != null) {
            val uniqueFileName = "poi_${poi.id}_${System.currentTimeMillis()}.jpg"
            val permanentPath = saveImageToInternalStorage(context, tempPhotoUri.value!!, uniqueFileName)
            if (permanentPath != null) {
                navController.navigate(
                    Route.PhotoReview(
                        photoUri = permanentPath,
                        poiId = poi.id,
                        poiName = poi.name,
                        calculatedPoints = calculatedPoints,
                        userEmail = currentUserEmail,
                        distanceKm = distance
                    )
                )
            }
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val file = File(context.externalCacheDir, "temp_photo.jpg")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                tempPhotoUri.value = uri
                cameraLauncher.launch(uri)
            }
        }

    val buttonContainerColor = Color.Transparent
    val buttonContentColor = MaterialTheme.colorScheme.onBackground
    val buttonBorder = BorderStroke(2.dp, buttonContentColor)

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).background(MaterialTheme.colorScheme.background)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(210.dp)
        ) {
            if (!savedPhotoUri.isNullOrEmpty()) {
                AsyncImage(
                    model = if (savedPhotoUri.startsWith("/")) File(savedPhotoUri) else savedPhotoUri.toUri(),
                    contentDescription = poi.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Nessuna tua foto caricata", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier.statusBarsPadding().padding(12.dp).size(40.dp).background(Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.Black)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = poi.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = {
                    scope.launch {
                        val currentStatus = poiDao.getPoiStatus(poi.id, currentUserEmail)
                        if (currentStatus != null) {
                            poiDao.insertOrUpdatePoiStatus(
                                currentStatus.copy(isFavorite = !isFavorite, poiName = poi.name)
                            )
                        } else {
                            poiDao.insertOrUpdatePoiStatus(
                                PoiStatus(
                                    userEmail = currentUserEmail,
                                    poiId = poi.id,
                                    poiName = poi.name,
                                    photoUri = null,
                                    visits = 0,
                                    stars = 0,
                                    isFavorite = true
                                )
                            )
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Preferito",
                        tint = if (isFavorite) Color(0xFFE91E63) else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Descrizione:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = poi.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)

            Spacer(modifier = Modifier.height(24.dp))

            if (isJustUploaded) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = BorderStroke(1.5.dp, Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Ottimo! +$calculatedPoints Punti ottenuti", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Dai un voto al luogo:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < selectedStars) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(38.dp).clickable {
                                        selectedStars = index + 1
                                        scope.launch {
                                            val currentStatus = poiDao.getPoiStatus(poi.id, currentUserEmail)
                                            if (currentStatus != null) {
                                                poiDao.insertOrUpdatePoiStatus(
                                                    currentStatus.copy(stars = index + 1, poiName = poi.name)
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PoiMetricCard(icon = Icons.AutoMirrored.Filled.DirectionsWalk, title = "Numero visite", value = "$visits", modifier = Modifier.weight(1f))
                        PoiMetricCard(icon = Icons.Default.EmojiEvents, title = "Punti da ottenere", value = "$calculatedPoints pt", modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(72.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("Tuo Voto", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = if (index < savedStars) Icons.Default.Star else Icons.Outlined.StarBorder,
                                            tint = if (savedStars > 0) Color(0xFFFFB300) else Color(0xFFD1D5DB),
                                            modifier = Modifier.size(18.dp),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                        PoiMetricCard(icon = Icons.Default.LocationOn, title = "Distanza da te", value = "${"%.2f".format(distance)} km", modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = {
                            val gmmIntentUri = "google.navigation:q=${poi.lat},${poi.lng}".toUri()
                            context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri).setPackage("com.google.android.apps.maps"))
                        },
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = buttonBorder,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = buttonContainerColor,
                            contentColor = buttonContentColor
                        )
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = buttonContentColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AVVIA", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                val file = File(context.externalCacheDir, "temp_photo.jpg")
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                tempPhotoUri.value = uri
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = buttonBorder,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = buttonContainerColor,
                            contentColor = buttonContentColor
                        )
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = buttonContentColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("POSTA", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun PoiMetricCard(icon: ImageVector, title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.height(72.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}