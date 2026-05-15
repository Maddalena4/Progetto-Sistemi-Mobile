package com.example.cityguest.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class CityData(val name: String, val requiredPoints: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    userPoints: Int = 0,
    onCityClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val cities = listOf(
        CityData("Forlì", 0),
        CityData("Roma", 500),
        CityData("Verona", 1000)
    )

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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cities) { city ->
                    val isUnlocked = userPoints >= city.requiredPoints

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(enabled = isUnlocked) {
                                onCityClick(city.name)
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
                                        text = "Città bloccata",
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
}