package com.vibeeco.app.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.ui.components.*
import com.vibeeco.app.ui.theme.GreenBrand
import com.vibeeco.app.ui.theme.VibeColors
import kotlinx.coroutines.delay

private const val RESEND_SECONDS = 30

@Composable
fun VerifyCodeScreen(
    email: String,
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
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
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
            title = { Text("Simulação de e-mail 📧", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Como este é um app de demonstração, o código gerado para $email é:")
                    Spacer(Modifier.height(12.dp))
                    Text(demoCode, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = GreenBrand)
                }
            },
            confirmButton = {
                TextButton(onClick = { shownFor = demoCode }) {
                    Text("Copiar e preencher", color = GreenBrand, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }

    AuthScaffold(
        title = "Verifique seu e-mail",
        subtitle = "Enviamos um código de 6 dígitos para\n$email",
        topAction = {
            Text(
                text = "← Voltar",
                color = Color(0xFF5B7A66),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onBack)
            )
        }
    ) {
        if (error != null) {
            VibeAlert(message = error!!, isError = true)
        }

        OtpCodeInput(
            value = code,
            onValueChange = {
                code = it
                error = null
                if (it.length == 6) verifyTrigger++
            },
            isError = error != null
        )

        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            text = if (loading) "Verificando..." else "Verificar código",
            onClick = { verifyTrigger++ },
            enabled = code.length == 6,
            loading = loading
        )

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Não recebeu? ", color = Color(0xFF829084), fontSize = 13.5.sp)
            if (secondsLeft > 0) {
                Text("Reenviar em ${secondsLeft}s", color = Color(0xFF829084), fontSize = 13.5.sp)
            } else {
                TextButton(
                    onClick = { code = ""; resendTrigger++ },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Reenviar código", color = GreenBrand, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        BackToLogin(onBack, "← Alterar e-mail")
    }
}
