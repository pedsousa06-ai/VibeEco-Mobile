package com.vibeeco.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.Levels
import com.vibeeco.app.data.Progress
import com.vibeeco.app.data.User
import com.vibeeco.app.data.formatCo2
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.VibeColors

private data class Badge(val emoji: String, val title: String, val unlocked: Boolean)

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    tint: Color = VibeColors.Green,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        Modifier.fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = tint)
        Spacer(Modifier.width(14.dp))
        Text(title, Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Medium,
            color = if (tint == VibeColors.Error) VibeColors.Error else VibeColors.Title)
        trailing()
    }
}

@Composable
fun ProfileScreen(
    user: User,
    progress: Progress,
    notifications: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
) {
    var confirmLogout by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    val badges = listOf(
        Badge("👣", "Primeiro passo", progress.actions >= 1),
        Badge("🔥", "Em chamas", progress.streak >= 3),
        Badge("📚", "Estudioso", progress.readArticles.size >= 3),
        Badge("🌳", "Árvore", progress.points >= 400),
        Badge("🌍", "Guardião", progress.co2Grams >= 20000),
        Badge("⭐", "Eco herói", progress.points >= 600),
    )

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        GradientHeader {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Avatar(user.name, 84.dp, background = Color.White, textColor = VibeColors.GreenDark)
                Spacer(Modifier.height(12.dp))
                Text(user.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp, textAlign = TextAlign.Center)
                Text(user.email, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                Text("Matrícula ${user.matricula}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "${Levels.emoji(progress.points)}  Nível ${Levels.level(progress.points)} · ${Levels.name(progress.points)}",
                    color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }
        }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("⭐", "${progress.points}", "Pontos", Modifier.weight(1f))
                StatTile("✅", "${progress.actions}", "Ações", Modifier.weight(1f))
                StatTile("🌍", formatCo2(progress.co2Grams), "CO₂ evitado", Modifier.weight(1f))
            }

            SectionTitle("Conquistas")
            badges.chunked(3).forEach { rowBadges ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowBadges.forEach { b ->
                        VibeCard(Modifier.weight(1f)) {
                            Column(
                                Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(if (b.unlocked) b.emoji else "🔒", fontSize = 28.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(b.title, fontSize = 12.sp, textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (b.unlocked) VibeColors.Title else VibeColors.Muted)
                            }
                        }
                    }
                }
            }

            SectionTitle("Configurações")
            VibeCard(Modifier.fillMaxWidth()) {
                SettingRow(Icons.Filled.Notifications, "Lembretes diários") {
                    Switch(
                        checked = notifications, onCheckedChange = onNotificationsChange,
                        colors = SwitchDefaults.colors(checkedTrackColor = VibeColors.Green),
                    )
                }
                HorizontalDivider(color = VibeColors.GreenSoft)
                SettingRow(Icons.Filled.Lock, "Alterar senha", onClick = onChangePassword)
                HorizontalDivider(color = VibeColors.GreenSoft)
                SettingRow(Icons.Filled.Info, "Sobre o VibeEco", onClick = { showAbout = true })
                HorizontalDivider(color = VibeColors.GreenSoft)
                SettingRow(Icons.AutoMirrored.Filled.ExitToApp, "Sair da conta",
                    tint = VibeColors.Error, onClick = { confirmLogout = true })
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text("Sair da conta?") },
            text = { Text("Você precisará entrar novamente para continuar sua jornada.") },
            confirmButton = {
                TextButton(onClick = { confirmLogout = false; onLogout() }) {
                    Text("Sair", color = VibeColors.Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Cancelar") } },
        )
    }
    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text("VibeEco 🌱") },
            text = { Text("Versão 1.0\nPlataforma de conscientização à sustentabilidade.\n\nCada pequena ação conta. Juntos, fazemos a diferença.") },
            confirmButton = { TextButton(onClick = { showAbout = false }) { Text("Fechar") } },
        )
    }
}
