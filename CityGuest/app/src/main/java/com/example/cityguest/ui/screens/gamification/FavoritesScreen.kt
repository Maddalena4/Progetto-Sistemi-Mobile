package com.example.cityguest.ui.screens.gamification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cityguest.data.poi.PoiDao
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FavoritesScreen(
    userEmail: String,
    poiDao: PoiDao,
    modifier: Modifier = Modifier,
    onPoiClick: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val favoritesState = poiDao.observeFavoritePois(userEmail).collectAsState(initial = emptyList())
    val favoriteList = favoritesState.value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        if (favoriteList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Cuore Vuoto",
                    modifier = Modifier.size(72.dp),
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nessun luogo preferito!",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Esplora la mappa e colleziona i tuoi preferiti.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 90.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "I tuoi luoghi preferiti",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(favoriteList, key = { it.poiId }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPoiClick(item.poiId) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE9ECEF)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!item.photoUri.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = if (item.photoUri.startsWith("/")) File(item.photoUri) else item.photoUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {

                                Text(
                                    text = item.poiName.ifEmpty { "Luogo #${item.poiId}" },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Visite: ${item.visits} • Voto: ${item.stars}⭐",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }

                            IconButton(onClick = {
                                scope.launch {
                                    poiDao.insertOrUpdatePoiStatus(item.copy(isFavorite = false))
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Rimuovi dai preferiti",
                                    tint = Color(0xFFE91E63)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}