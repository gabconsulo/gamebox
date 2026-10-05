package com.example.letterbox_games.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Conjunto de cores do app. Existem duas paletas (escura e clara) e todas as telas
 * leem as cores pelo objeto Cores, então trocar o modo muda o app inteiro de uma vez.
 */
class Paleta(
    val escuro: Boolean,
    val fundoTopo: Color,
    val fundoBase: Color,
    val barra: Color,
    val cartao: Color,
    val borda: Color,
    val texto: Color,
    val cinza: Color,
    val cinzaEscuro: Color,
    val destaque: Color,
    val sobreDestaque: Color,
    val chipSelecionado: Color,
    val roxo: Color,
    val gradienteFim: Color,
    val estrela: Color,
    val estrelaVazia: Color,
    val perigo: Color,
    val sucesso: Color
)

val PaletaEscura = Paleta(
    escuro = true,
    fundoTopo = Color(0xFF070510),
    fundoBase = Color(0xFF140E28),
    barra = Color(0xFF0D0B1A),
    cartao = Color(0xB3171329),
    borda = Color(0x14FFFFFF),
    texto = Color.White,
    cinza = Color(0xFF9CA3AF),
    cinzaEscuro = Color(0xFF6B7280),
    destaque = Color(0xFF00F3FF),
    sobreDestaque = Color(0xFF0D0D26),
    chipSelecionado = Color(0xA1221C3A),
    roxo = Color(0xFF8B5CF6),
    gradienteFim = Color(0xFF00F3FF),
    estrela = Color(0xFFFBBF24),
    estrelaVazia = Color(0xFF4B5563),
    perigo = Color(0xFFFF6B6B),
    sucesso = Color(0xFF34D399)
)

val PaletaClara = Paleta(
    escuro = false,
    fundoTopo = Color(0xFFF7F5FF),
    fundoBase = Color(0xFFECE8FA),
    barra = Color(0xFFFFFFFF),
    cartao = Color(0xFFFFFFFF),
    borda = Color(0x1F1B1530),
    texto = Color(0xFF1B1530),
    cinza = Color(0xFF5B6475),
    cinzaEscuro = Color(0xFF8A93A3),
    destaque = Color(0xFF0E7490),
    sobreDestaque = Color(0xFFFFFFFF),
    chipSelecionado = Color(0xFFE0F4F8),
    roxo = Color(0xFF7C3AED),
    gradienteFim = Color(0xFF06B6D4),
    estrela = Color(0xFFF59E0B),
    estrelaVazia = Color(0xFFD1D5DB),
    perigo = Color(0xFFDC2626),
    sucesso = Color(0xFF059669)
)

val LocalPaleta = staticCompositionLocalOf { PaletaEscura }
