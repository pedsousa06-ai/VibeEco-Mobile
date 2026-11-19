
package com.vibeeco.app.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vibeeco.app.data.AppConfig
import com.vibeeco.app.data.Challenge

@Composable
fun HomeRoute(
    userId: Int = AppConfig.DEV_USER_ID,
    onToggleChallenge: (Challenge) -> Unit,
    onSeeAllChallenges: () -> Unit,
    onSeeAllMissions: () -> Unit,
    onOpenMission: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLearn: () -> Unit,
    onOpenNotifications: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    LaunchedEffect(userId) {
        Log.d("VIBEECO_API", "Carregando Home do usuário: $userId")
        viewModel.carregarDados(userId)
    }

    val state by viewModel.uiState.collectAsState()

    when (val currentState = state) {

        is HomeViewModel.HomeUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Carregando seus dados...")
                }
            }
        }

        is HomeViewModel.HomeUiState.Success -> {
            val dados = currentState.data

            HomeScreen(
                usuario = dados.usuario,
                streak = currentState.streakDias,
                desafios = currentState.desafiosConvertidos,
                missoes = currentState.missoesConvertidas,
                conteudos = dados.conteudos.orEmpty(),
                conquistas = dados.conquistas.orEmpty(),
                onSeeAll = onSeeAllChallenges,
                onSeeAllMissions = onSeeAllMissions,
                onOpenMission = onOpenMission,
                onOpenProfile = onOpenProfile,
                onOpenLearn = onOpenLearn,
                onOpenNotifications = onOpenNotifications
            )
        }

        is HomeViewModel.HomeUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Não foi possível carregar a tela inicial.",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(currentState.message)
                }
            }
        }
    }
}
