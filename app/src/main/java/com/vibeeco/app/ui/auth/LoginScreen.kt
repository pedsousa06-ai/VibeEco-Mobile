package com.vibeeco.app.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.data.AppConfig
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.GreenBrand
import com.vibeeco.app.ui.theme.GreenBrandLight
import com.vibeeco.app.ui.theme.VibeColors

@Composable
fun LoginScreen(
    /** Retorna null se o login deu certo, ou a mensagem de erro. */
    onLogin: suspend (user: String, password: String, remember: Boolean) -> String?,
    onForgotPassword: () -> Unit,
    onFirstAccess: (() -> Unit)? = null,
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
        subtitle = "Entre com suas credenciais para acessar a plataforma.",
    ) {
        // Alerta de erro estilizado
        if (error != null) {
            VibeAlert(message = error!!, isError = true)
        }

        VibeTextField(
            label = "E-mail ou matrícula",
            value = user,
            placeholder = "seuemail@instituicao.edu.br",
            onValueChange = { user = it; error = null },
            keyboardType = KeyboardType.Email,
        )

        Spacer(Modifier.height(18.dp))

        VibeTextField(
            label = "Senha",
            value = password,
            placeholder = "Digite sua senha",
            onValueChange = { password = it; error = null },
            isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = { if (canSubmit && !loading) trigger++ },
        )

        Spacer(Modifier.height(10.dp))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { rememberMe = !rememberMe }
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GreenBrand,
                        uncheckedColor = Color(0xFF829084)
                    ),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "lembrar de mim",
                    fontSize = 13.sp,
                    color = Color(0xFF68785B),
                )
            }

            TextButton(
                onClick = onForgotPassword,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Esqueci minha senha",
                    color = GreenBrand,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.5.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            text = if (loading) "Entrando..." else "Entrar na plataforma",
            onClick = { trigger++ },
            enabled = canSubmit,
            loading = loading
        )

        if (onFirstAccess != null) {
            Spacer(Modifier.height(22.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFF829084), fontSize = 13.sp)) {
                            append("Primeiro acesso? ")
                        }
                        withStyle(
                            SpanStyle(
                                color = GreenBrand,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        ) {
                            append("Crie sua senha aqui")
                        }
                    },
                    modifier = Modifier.clickable(onClick = onFirstAccess)
                )
            }
        }
    }
}
