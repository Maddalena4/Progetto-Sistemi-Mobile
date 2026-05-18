package com.example.cityguest.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cityguest.data.PoiData

data class CityData(val name: String, val requiredPoints: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    userPoints: Int = 0,
    unlockedCities: List<String> = emptyList(),
    onCityClick: (String) -> Unit,
    onUnlockCity: (CityData) -> Unit,
    onBack: () -> Unit
) {
    val cities = remember {
        PoiData.pointsOfInterest
            .groupBy { it.imageRes }
            .map { (cityName, pois) ->
                val cost = if (cityName.equals("Forlì", ignoreCase = true)) {
                    0
                } else {
                    pois.sumOf { it.basePoints } * 100
                }
                CityData(name = cityName, requiredPoints = cost)
            }
            .sortedBy { it.requiredPoints }
    }

    var showUnlockDialog by remember { mutableStateOf(false) }
    var showInsufficientPointsDialog by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<CityData?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SELEZIONA CITTÀ",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Punti",
                            tint = Color(0xFFFFB300)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I tuoi Punti",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = "$userPoints",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cities) { city ->
                    val isUnlocked = city.requiredPoints == 0 || unlockedCities.contains(city.name)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (isUnlocked) {
                                    onCityClick(city.name)
                                } else {
                                    selectedCity = city
                                    showUnlockDialog = true
                                }
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = city.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) MaterialTheme.colorScheme.onBackground else Color.Gray
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                if (isUnlocked) {
                                    Text(
                                        text = "Città sbloccata",
                                        color = Color(0xFF4CAF50),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else {
                                    Text(
                                        text = "Costo: ${city.requiredPoints} punti",
                                        color = Color.Red.copy(alpha = 0.7f),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isUnlocked) MaterialTheme.colorScheme.onBackground else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showUnlockDialog && selectedCity != null) {
        AlertDialog(
            onDismissRequest = { showUnlockDialog = false },
            title = { Text("Sblocca Città") },
            text = { Text("Vuoi sbloccare ${selectedCity!!.name}? Costa ${selectedCity!!.requiredPoints} punti.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUnlockDialog = false
                        if (userPoints >= selectedCity!!.requiredPoints) {
                            onUnlockCity(selectedCity!!)
                        } else {
                            showInsufficientPointsDialog = true
                        }
                    }
                ) {
                    Text("Sì")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlockDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }

    if (showInsufficientPointsDialog) {
        AlertDialog(
            onDismissRequest = { showInsufficientPointsDialog = false },
            title = { Text("Punti insufficienti") },
            text = { Text("Non hai abbastanza punti per sbloccare questa città.") },
            confirmButton = {
                TextButton(onClick = { showInsufficientPointsDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}