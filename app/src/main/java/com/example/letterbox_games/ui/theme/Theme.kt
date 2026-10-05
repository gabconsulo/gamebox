package com.example.letterbox_games.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val EsquemaEscuro = darkColorScheme(
    primary = Color(0xFF00F3FF),
    onPrimary = Color(0xFF0D0D26),
    secondary = Color(0xFF8B5CF6),
    background = Color(0xFF070510),
    onBackground = Color.White,
    surface = Color(0xFF140E28),
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF9CA3AF),
    surfaceContainerHigh = Color(0xFF221C3A)
)

private val EsquemaClaro = lightColorScheme(
    primary = Color(0xFF7C3AED),
    onPrimary = Color.White,
    secondary = Color(0xFF0E7490),
    background = Color(0xFFF7F5FF),
    onBackground = Color(0xFF1B1530),
    surface = Color(0xFFF7F5FF),
    onSurface = Color(0xFF1B1530),
    onSurfaceVariant = Color(0xFF5B6475),
    surfaceContainerHigh = Color.White
)

/**
 * Tema do app. O modo (escuro/claro) vem de fora porque o usuário escolhe no Perfil.
 * Além do MaterialTheme (usado por diálogos e barras), entrega a nossa Paleta.
 */
@Composable
fun LetterboxgamesTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalPaleta provides if (darkTheme) PaletaEscura else PaletaClara) {
        MaterialTheme(
            colorScheme = if (darkTheme) EsquemaEscuro else EsquemaClaro,
            typography = Typography,
            content = content
        )
    }
}
