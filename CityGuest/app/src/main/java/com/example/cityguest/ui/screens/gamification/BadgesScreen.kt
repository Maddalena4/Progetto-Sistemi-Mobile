package com.example.cityguest.ui.screens.gamification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modello dati che rappresenta un singolo distintivo (Badge) ottenibile nel gioco.
 *
 * @property title Il titolo identificativo del badge.
 * @property description Breve spiegazione dell'obiettivo raggiunto.
 * @property requiredPoints I punti cumulativi minimi necessari per sbloccare questo badge.
 * @property color Il colore tematico associato al badge.
 */
data class Badge(
    val title: String,
    val description: String,
    val requiredPoints: Int,
    val color: Color
)

/**
 * Elenco statico di tutti i badge disponibili all'interno dell'applicazione.
 */
val allBadges = listOf(
    Badge("Esploratore",   "Hai raggiunto 1.000 punti",  1_000,  Color(0xFF4CAF50)),
    Badge("Viaggiatore",   "Hai raggiunto 2.000 punti",  2_000,  Color(0xFF2196F3)),
    Badge("Avventuriero",  "Hai raggiunto 3.000 punti",  3_000,  Color(0xFF9C27B0)),
    Badge("Scopritore",    "Hai raggiunto 5.000 punti",  5_000,  Color(0xFFFF9800)),
    Badge("Leggenda",      "Hai raggiunto 10.000 punti", 10_000, Color(0xFFFFD700))
)

/**
 * Schermata di gamification che mostra il saldo punti totale dell'utente e
 * una griglia con tutti i badge sbloccabili. I badge non ancora ottenuti
 * appaiono come disabilitati e accompagnati da un lucchetto.
 *
 * @param userPoints I punti correnti dell'utente, usati per calcolare i badge sbloccati.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    userPoints: Int
) {
    val earnedCount = allBadges.count { userPoints >= it.requiredPoints }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "I tuoi punti",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$userPoints pt",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Badge ottenuti",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$earnedCount / ${allBadges.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "BADGE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allBadges) { badge ->
                val earned = userPoints >= badge.requiredPoints
                BadgeCard(badge = badge, earned = earned)
            }
        }
    }
}

/**
 * Componente UI che renderizza la card singola di un Badge.
 *
 * @param badge Il modello dati del Badge da mostrare.
 * @param earned Booleano che determina se l'utente possiede o meno il badge.
 */
@Composable
private fun BadgeCard(badge: Badge, earned: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (earned)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (earned) 4.dp else 0.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = if (earned) badge.color.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (earned) Icons.Default.EmojiEvents else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (earned) badge.color else Color.Gray.copy(alpha = 0.4f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = badge.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = if (earned)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = badge.description,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                color = if (earned)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (earned) badge.color.copy(alpha = 0.12f) else Color.Gray.copy(alpha = 0.08f)
            ) {
                Text(
                    text = "${badge.requiredPoints / 1000}k pt",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (earned) badge.color else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}