package com.example.letterbox_games

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.example.letterbox_games.navigation.AppNavigation
import com.example.letterbox_games.state.EstadoApp
import com.example.letterbox_games.ui.theme.LetterboxgamesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Todos os dados do app ficam nesta única instância (em memória).
            val estado = remember { EstadoApp() }

            // Sem escolha feita no Perfil, o app segue o tema do celular.
            val escuro = estado.modoEscuro ?: isSystemInDarkTheme()

            // Mantém os ícones da barra de status/navegação legíveis no modo escolhido.
            DisposableEffect(escuro) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { escuro },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                        Color.argb(0x80, 0x1b, 0x1b, 0x1b)
                    ) { escuro }
                )
                onDispose {}
            }

            LetterboxgamesTheme(darkTheme = escuro) {
                AppNavigation(estado = estado)
            }
        }
    }
}
