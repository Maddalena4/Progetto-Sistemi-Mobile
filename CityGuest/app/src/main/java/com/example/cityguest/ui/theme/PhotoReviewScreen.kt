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
            AsyncImage(
                model = args.photoUri.toUri(),
                contentDescription = "Anteprima",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            Text(
                text = args.poiName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Ti piace questo scatto?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Confermando, salverai la foto per questo luogo.", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
                                val currentStars = currentStatus?.stars ?: 5

                                val updatedStatus = PoiStatus(
                                    userEmail = args.userEmail,
                                    poiId = args.poiId,
                                    photoUri = args.photoUri,
                                    visits = newVisits,
                                    stars = currentStars
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