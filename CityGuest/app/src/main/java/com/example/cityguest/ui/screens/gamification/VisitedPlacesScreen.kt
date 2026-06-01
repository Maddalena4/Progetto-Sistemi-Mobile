package com.example.cityguest.ui.screens.gamification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cityguest.data.poi.PoiVisit
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

/**
 * Schermata adibita alla visualizzazione cronologica delle visite effettuate con successo dall'utente nei vari Punti di Interesse.
 *
 * @param visits Lista di oggetti [PoiVisit] estratti dal database locale.
 * @param onBack Callback per retrocedere nella navigazione.
 * @param onPoiClick Callback scatenata quando l'utente clicca su un luogo visitato, permettendo la navigazione al relativo dettaglio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitedPlacesScreen(
    visits: List<PoiVisit>,
    onBack: () -> Unit,
    onPoiClick: (Int) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", LocalLocale.current.platformLocale)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Luoghi Visitati", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Gestione dello stato vuoto (Empty State): se l'utente non ha ancora visitato nulla, mostra un feedback grafico
        if (visits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Non hai ancora visitato nessun luogo.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(visits) { visit ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPoiClick(visit.poiId) }, // Rende l'intera card cliccabile usando il poiId della visita
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2E7D32))
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(visit.poiName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                Text(dateFormat.format(Date(visit.timestamp)), fontSize = 12.sp, color = Color.Gray)
                            }

                            // Visualizzazione della distanza fisica calcolata dall'algoritmo GPS durante l'aggancio del POI
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.DirectionsWalk, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(16.dp))
                                Text("${"%.2f".format(visit.distanceKm)} km", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }
    }
}