package com.vibeeco.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.components.*
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
        subtitle = "Digite seu e-mail e enviaremos um link para redefinir sua senha.",
    ) {
        VibeTextField(
            label = "E-mail cadastrado na instituição",
            value = email,
            onValueChange = { email = it; error = null; sent = false },
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            onImeAction = { if (isValidEmail(email) && !sent) trigger++ },
            error = error ?: if (email.isNotEmpty() && !isValidEmail(email)) "E-mail inválido" else null,
        )
        Spacer(Modifier.height(16.dp))
        if (!sent) {
            PrimaryButton("Enviar Link de recuperação", onClick = { trigger++ },
                enabled = isValidEmail(email), loading = loading)
        } else {
            PrimaryButton("Inserir código recebido", onClick = { onContinueToCode(email.trim()) })
            Spacer(Modifier.height(14.dp))
            Text("Link enviado com sucesso!", color = VibeColors.Success, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Text("Verifique sua caixa de entrada em ${email.trim()}", color = VibeColors.Muted, fontSize = 13.sp)
        }
        Spacer(Modifier.height(48.dp))
        BackToLogin(onBackToLogin)
    }
}