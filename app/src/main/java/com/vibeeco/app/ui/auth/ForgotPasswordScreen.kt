package com.vibeeco.app.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.GreenBrand
import com.vibeeco.app.ui.theme.VibeColors

@Composable
fun ForgotPasswordScreen(
    /** Retorna null se o envio deu certo, ou a mensagem de erro. */
    onSendLink: suspend (email: String) -> String?,
    onContinueToCode: (email: String) -> Unit,
    onBackToLogin: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var trigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        loading = true
        val result = onSendLink(email.trim())
        loading = false
        if (result == null) sent = true else error = result
    }

    AuthScaffold(
        title = "Esqueceu sua senha?",
        subtitle = "Informe o e-mail cadastrado e enviaremos um código para redefinir sua senha.",
        topAction = {
            Text(
                text = "← Voltar ao login",
                color = Color(0xFF5B7A66),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onBackToLogin)
            )
        }
    ) {
        if (error != null) {
            VibeAlert(message = error!!, isError = true)
        }

        if (sent) {
            VibeAlert(
                message = "Código enviado com sucesso! Verifique sua caixa de entrada em ${email.trim()}",
                isError = false
            )
        }

        VibeTextField(
            label = "E-mail institucional",
            placeholder = "seuemail@instituicao.edu.br",
            value = email,
            onValueChange = { email = it; error = null; sent = false },
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            onImeAction = { if (isValidEmail(email) && !sent && !loading) trigger++ },
            error = if (email.isNotEmpty() && !isValidEmail(email)) "Formato de e-mail inválido" else null,
        )

        Spacer(Modifier.height(20.dp))

        if (!sent) {
            PrimaryButton(
                text = if (loading) "Enviando..." else "Enviar código de recuperação",
                onClick = { trigger++ },
                enabled = isValidEmail(email),
                loading = loading
            )
        } else {
            PrimaryButton(
                text = "Inserir código recebido",
                onClick = { onContinueToCode(email.trim()) }
            )
        }

        Spacer(Modifier.height(16.dp))
        BackToLogin(onBackToLogin)
    }
}
