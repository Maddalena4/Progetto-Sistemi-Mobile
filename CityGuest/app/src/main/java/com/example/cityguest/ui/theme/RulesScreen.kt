package com.example.cityguest.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RulesScreen(onBack: () -> Unit) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            // HEADER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Regole del Gioco",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Scopri come diventare un esploratore esperto",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // RULES
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {

                RuleItem(
                    icon = Icons.Default.LockOpen,
                    title = "Sblocca le Città",
                    description = "Inizia da una città e accumula punti per sbloccare nuove mete come Roma e Verona."
                )

                RuleItem(
                    icon = Icons.Default.Explore,
                    title = "Esplora la Mappa",
                    description = "Cerca i segnalini sulla mappa per individuare i Punti di Interesse (POI)."
                )

                RuleItem(
                    icon = Icons.Default.LocationOn,
                    title = "Raggiungi i Luoghi",
                    description = "Devi recarti fisicamente nel punto indicato per convalidare la visita."
                )

                RuleItem(
                    icon = Icons.Default.EmojiEvents,
                    title = "Guadagna Punti",
                    description = "Ogni luogo visitato ti regala punti preziosi per scalare la classifica!"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BUTTON
            Button(
                onClick = onBack,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape = RoundedCornerShape(16.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                ),

                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {

                Text(
                    text = "HO CAPITO, COMINCIAMO!",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun RuleItem(
    icon: ImageVector,
    title: String,
    description: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ICON CIRCLE
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,

                    tint = MaterialTheme.colorScheme.onBackground,

                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,

                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),

                    lineHeight = 18.sp
                )
            }
        }
    }
}