package com.vibeeco.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.VibeColors
import kotlinx.coroutines.delay

private const val RESEND_SECONDS = 30

@Composable
fun VerifyCodeScreen(
    email: String,
    /** Código gerado (modo demonstração: mostrado em um aviso, já que não há envio real de e-mail). */
    demoCode: String?,
    onVerify: suspend (email: String, code: String) -> Boolean,
    onResend: suspend (email: String) -> Unit,
    onVerified: () -> Unit,
    onBack: () -> Unit,
) {
    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var verifyTrigger by remember { mutableIntStateOf(0) }
    var resendTrigger by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(RESEND_SECONDS) }
    var shownFor by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(resendTrigger) {
        if (resendTrigger > 0) onResend(email)
        secondsLeft = RESEND_SECONDS
        while (secondsLeft > 0) { delay(1000); secondsLeft-- }
    }

    LaunchedEffect(verifyTrigger) {
        if (verifyTrigger == 0) return@LaunchedEffect
        loading = true
        val ok = onVerify(email, code)
        loading = false
        if (ok) onVerified() else error = "Código inválido ou expirado."
    }

    if (demoCode != null && shownFor != demoCode) {
        AlertDialog(
            onDismissRequest = { shownFor = demoCode },
            title = { Text("Simulação de e-mail 📧") },
            text = {
                Column {
                    Text("Como este é um app de demonstração, o código que seria enviado para $email é:")
                    Spacer(Modifier.height(12.dp))
                    Text(demoCode, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = VibeColors.Green)
                }
            },
            confirmButton = { TextButton(onClick = { shownFor = demoCode }) { Text("Entendi") } },
        )
    }

    AuthScaffold(
        title = "Verifique seu e-mail",
        subtitle = "Enviamos um código de 6 dígitos para\n$email",
    ) {
        OtpCodeInput(code, { code = it; error = null; if (it.length == 6) verifyTrigger++ }, isError = error != null)
        if (error != null) {
            Text(error!!, color = VibeColors.Error, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Verificar código", onClick = { verifyTrigger++ }, enabled = code.length == 6, loading = loading)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Text("Não recebeu? ", color = VibeColors.Muted, fontSize = 14.sp)
            if (secondsLeft > 0) {
                Text("Reenviar em ${secondsLeft}s", color = VibeColors.Muted, fontSize = 14.sp)
            } else {
                TextButton(onClick = { code = ""; resendTrigger++ }, contentPadding = PaddingValues(0.dp)) {
                    Text("Reenviar código", color = VibeColors.Success, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        BackToLogin(onBack, "← Alterar e-mail")
    }
}
