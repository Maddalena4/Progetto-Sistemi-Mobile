package com.example.cityguest.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cityguest.data.PoiDao
import com.example.cityguest.navigation.Route
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.example.cityguest.data.PoiStatus
import java.io.File

@Composable
fun PhotoReviewScreen(
    args: Route.PhotoReview,
    poiDao: PoiDao,
    onRetry: () -> Unit,
    onUploadSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val modelToLoad: Any = if (args.photoUri.startsWith("/")) {
                File(args.photoUri)
            } else {
                args.photoUri.toUri()
            }

            AsyncImage(
                model = modelToLoad,
                contentDescription = "Anteprima Foto",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Vuoi pubblicare questa foto?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = args.poiName,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Text("RIPROVA", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            scope.launch {

                                val currentStatus = poiDao.getPoiStatus(args.poiId, args.userEmail)

                                val newVisits = (currentStatus?.visits ?: 0) + 1
                                val currentStars = currentStatus?.stars ?: 0
                                val currentFavorite = currentStatus?.isFavorite ?: false

                                val updatedStatus = PoiStatus(
                                    userEmail = args.userEmail,
                                    poiId = args.poiId,
                                    poiName = args.poiName,
                                    photoUri = args.photoUri,
                                    visits = newVisits,
                                    stars = currentStars,
                                    isFavorite = currentFavorite
                                )

                                poiDao.insertOrUpdatePoiStatus(updatedStatus)
                                onUploadSuccess()
                            }
                        },
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("CARICA", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}