package com.vibeeco.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vibeeco.app.navigation.VibeNavHost
import com.vibeeco.app.ui.theme.VibeEcoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            // ícones claros na barra de status (cabeçalhos verdes) e escuros na barra de navegação (barra branca)
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent { VibeEcoTheme { VibeNavHost() } }
    }
}
