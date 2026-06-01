package com.example.cityguest.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.EmojiEvents
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
                    pois.sumOf { it.basePoints }
                }
                CityData(name = cityName, requiredPoints = cost)
            }
            .sortedBy { it.requiredPoints }
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredCities = remember(searchQuery, cities) {
        cities.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val showUnlockDialog = remember { mutableStateOf(false) }
    val showInsufficientPointsDialog = remember { mutableStateOf(false) }
    val selectedCity = remember { mutableStateOf<CityData?>(null) }

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
        },
        bottomBar = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Punti",
                            tint = Color(0xFFFFD700)
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Cerca città...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cerca") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCities) { city ->
                    val isUnlocked = city.requiredPoints == 0 || unlockedCities.contains(city.name)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (isUnlocked) {
                                    onCityClick(city.name)
                                } else {
                                    selectedCity.value = city
                                    showUnlockDialog.value = true
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
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(
                                        text = "Costo: ${city.requiredPoints} punti",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = if (isUnlocked) "Stato" else "Bloccata",
                                tint = if (isUnlocked) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    if (showUnlockDialog.value && selectedCity.value != null) {
        AlertDialog(
            onDismissRequest = { showUnlockDialog.value = false },
            title = { Text("Sblocca ${selectedCity.value!!.name}") },
            text = { Text("Vuoi spendere ${selectedCity.value!!.requiredPoints} punti per sbloccare questa città?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUnlockDialog.value = false
                        if (userPoints >= selectedCity.value!!.requiredPoints) {
                            onUnlockCity(selectedCity.value!!)
                        } else {
                            showInsufficientPointsDialog.value = true
                        }
                    }
                ) { Text("Sblocca") }
            },
            dismissButton = {
                TextButton(onClick = { showUnlockDialog.value = false }) { Text("Annulla") }
            }
        )
    }

    if (showInsufficientPointsDialog.value) {
        AlertDialog(
            onDismissRequest = { showInsufficientPointsDialog.value = false },
            title = { Text("Punti insufficienti") },
            text = { Text("Non hai abbastanza punti per sbloccare questa città.") },
            confirmButton = {
                TextButton(onClick = { showInsufficientPointsDialog.value = false }) { Text("OK") }
            }
        )
    }
}