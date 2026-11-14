package com.vibeeco.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.AppConfig
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.VibeColors

@Composable
fun LoginScreen(
    /** Retorna null se o login deu certo, ou a mensagem de erro. */
    onLogin: suspend (user: String, password: String, remember: Boolean) -> String?,
    onForgotPassword: () -> Unit,
) {
    var user by remember { mutableStateOf(if (AppConfig.SIMULATED_LOGIN) AppConfig.DEMO_EMAIL else "") }
    var password by remember { mutableStateOf(if (AppConfig.SIMULATED_LOGIN) AppConfig.DEMO_PASSWORD else "") }
    var rememberMe by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var trigger by remember { mutableIntStateOf(0) }

    val canSubmit = user.isNotBlank() && password.isNotBlank()

    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        loading = true
        error = onLogin(user.trim(), password, rememberMe)
        loading = false
    }

    AuthScaffold(
        title = "Bem-vindo ao VibeEco",
        subtitle = "Acesse sua conta para continuar sua jornada.",
    ) {
        VibeTextField(
            label = "E-mail ou matrícula",
            value = user,
            onValueChange = { user = it; error = null },
            keyboardType = KeyboardType.Email,
        )
        Spacer(Modifier.height(16.dp))
        VibeTextField(
            label = "Senha",
            value = password,
            onValueChange = { password = it; error = null },
            isPassword = true,
            error = error,
            imeAction = ImeAction.Done,
            onImeAction = { if (canSubmit) trigger++ },
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(checkedColor = VibeColors.Green),
            )
            Text(
                text = "Lembrar de mim",
                fontSize = 14.sp,
                color = VibeColors.Label,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onForgotPassword) {
                Text(
                    text = "Esqueci minha senha",
                    color = VibeColors.Success,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimaryButton(
            text = "Entrar na plataforma",
            onClick = { trigger++ },
            enabled = canSubmit,
            loading = loading
        )
    }
}