package com.kutira.kone.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = FabricPlum,
    onPrimary = Color.White,
    secondary = FabricTeal,
    tertiary = FabricAmber,
    background = FabricSand,
    surface = Color.White,
    onSurface = FabricBrown,
    primaryContainer = Color(0xFFE1BEE7),
    secondaryContainer = Color(0xFFB2DFDB)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD1C4E9),
    onPrimary = Color(0xFF311B92),
    secondary = Color(0xFF80CBC4),
    tertiary = Color(0xFFFFE082),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFECEFF1),
    primaryContainer = Color(0xFF4527A0),
    secondaryContainer = Color(0xFF00695C)
)

@Composable
fun KutiraKoneTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        useDarkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KutiraTypography,
        content = content
    )
}
