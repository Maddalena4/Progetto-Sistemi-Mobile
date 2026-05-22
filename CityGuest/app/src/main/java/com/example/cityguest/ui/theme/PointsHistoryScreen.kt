package com.example.cityguest.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class PointTransaction(
    val title: String,
    val points: Int,
    val timestamp: Long,
    val isExpense: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsHistoryScreen(
    transactions: List<PointTransaction>,
    onBack: () -> Unit
) {
    Scaffold { paddingValues ->
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Nessuna transazione registrata.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions) { transaction ->
                    val dateStr = remember(transaction.timestamp) {
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(transaction.timestamp))
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(transaction.title, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(dateStr, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700))
                                Spacer(modifier = Modifier.width(4.dp))

                                val textColor = if (transaction.isExpense) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                                val sign = if (transaction.isExpense) "-" else "+"

                                Text(
                                    text = "$sign${transaction.points} pt",
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}