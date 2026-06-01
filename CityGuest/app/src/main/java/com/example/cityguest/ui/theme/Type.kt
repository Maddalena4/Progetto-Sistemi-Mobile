package com.example.cityguest.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Definizione degli stili tipografici dell'applicazione basati su Material Design 3.
 *
 * Configura i font, i pesi e le dimensioni del testo in modo da mantenere
 * un'interfaccia utente coerente in tutte le schermate dell'app.
 */
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)