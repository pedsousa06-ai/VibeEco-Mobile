package com.vibeeco.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vibeeco.app.navigation.VibeNavHost
import com.vibeeco.app.ui.theme.VibeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // App é SEMPRE claro.
        // isDark = false força ícones escuros na status bar e navigation bar,
        // independentemente do modo do sistema. Sem isso, no modo escuro do
        // celular os ícones ficam brancos e desaparecem no fundo claro do app.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
        )

        setContent {
            VibeTheme {
                VibeNavHost()
            }
        }
    }
}