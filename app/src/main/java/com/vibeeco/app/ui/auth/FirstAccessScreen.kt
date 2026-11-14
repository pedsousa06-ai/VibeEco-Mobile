package com.vibeeco.app.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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

@Composable
fun FirstAccessScreen(
    initialEmail: String = "",
    onVerifyUser: suspend (identifier: String) -> Pair<Boolean, String?>,
    onComplete: suspend (identifier: String, currentPassword: String?, newPassword: String) -> String?,
    onSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    var step by remember { mutableIntStateOf(if (initialEmail.isNotBlank()) 2 else 1) }
    var identifier by remember { mutableStateOf(initialEmail) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var triggerStep1 by remember { mutableIntStateOf(0) }
    var triggerStep2 by remember { mutableIntStateOf(0) }

    val rules = validatePassword(newPassword)
    val matches = newPassword == confirmPassword
    val canSubmitStep1 = identifier.isNotBlank()
    val canSubmitStep2 = rules.isValid && confirmPassword.isNotEmpty() && matches

    // Executa validação do Passo 1 (E-mail / Matrícula)
    LaunchedEffect(triggerStep1) {
        if (triggerStep1 == 0) return@LaunchedEffect
        loading = true
        error = null
        val (ok, err) = onVerifyUser(identifier.trim())
        loading = false
        if (ok) {
            step = 2
        } else {
            error = err ?: "Conta não encontrada na instituição."
        }
    }

    // Executa alteração da senha no Passo 2
    LaunchedEffect(triggerStep2) {
        if (triggerStep2 == 0) return@LaunchedEffect
        loading = true
        error = null
        val err = onComplete(identifier.trim(), currentPassword.ifBlank { null }, newPassword)
        loading = false
        if (err == null) {
            onSuccess()
        } else {
            error = err
        }
    }

    AuthScaffold(
        title = if (step == 1) "Primeiro Acesso" else "Crie sua nova senha",
        subtitle = if (step == 1)
            "Informe seu e-mail institucional ou matrícula para ativar sua conta."
        else
            "Defina uma senha forte para proteger seu acesso.",
        topAction = {
            if (step == 1) {
                Text(
                    text = "← Voltar ao login",
                    color = Color(0xFF5B7A66),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(onClick = onBackToLogin)
                )
            } else {
                Text(
                    text = "← Alterar e-mail",
                    color = Color(0xFF5B7A66),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        step = 1
                        error = null
                    }
                )
            }
        }
    ) {
        if (error != null) {
            VibeAlert(message = error!!, isError = true)
        }

        AnimatedContent(
            targetState = step,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "first_access_step"
        ) { currentStep ->
            if (currentStep == 1) {
                // ==========================================
                // PASSO 1: E-MAIL OU MATRÍCULA
                // ==========================================
                Column {
                    VibeTextField(
                        label = "E-mail ou matrícula institucional",
                        value = identifier,
                        onValueChange = { identifier = it; error = null },
                        placeholder = "seuemail@instituicao.edu.br",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )

                    Spacer(Modifier.height(16.dp))

                    VibeTextField(
                        label = "Senha provisória (opcional se fornecida pela instituição)",
                        value = currentPassword,
                        onValueChange = { currentPassword = it; error = null },
                        placeholder = "Digite a senha temporária (se possuir)",
                        isPassword = true,
                        imeAction = ImeAction.Done,
                        onImeAction = { if (canSubmitStep1 && !loading) triggerStep1++ }
                    )

                    Spacer(Modifier.height(24.dp))

                    PrimaryButton(
                        text = if (loading) "Verificando..." else "Continuar",
                        onClick = { triggerStep1++ },
                        enabled = canSubmitStep1,
                        loading = loading
                    )

                    Spacer(Modifier.height(14.dp))
                    BackToLogin(onBackToLogin)
                }
            } else {
                // ==========================================
                // PASSO 2: NOVA SENHA E CONFIRMAÇÃO
                // ==========================================
                Column {
                    VibeTextField(
                        label = "Nova Senha",
                        value = newPassword,
                        onValueChange = { newPassword = it; error = null },
                        placeholder = "Digite sua nova senha",
                        isPassword = true,
                        imeAction = ImeAction.Next
                    )

                    PasswordStrengthIndicator(rules, visible = newPassword.isNotEmpty())

                    Spacer(Modifier.height(14.dp))

                    val confirmMismatch = confirmPassword.isNotEmpty() &&
                        (confirmPassword.length >= newPassword.length || !newPassword.startsWith(confirmPassword)) &&
                        !matches

                    VibeTextField(
                        label = "Confirmar Nova Senha",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; error = null },
                        placeholder = "Confirme sua nova senha",
                        isPassword = true,
                        imeAction = ImeAction.Done,
                        onImeAction = { if (canSubmitStep2 && !loading) triggerStep2++ },
                        error = if (confirmMismatch) "As senhas não coincidem" else null
                    )

                    PasswordRequirements(rules)

                    Spacer(Modifier.height(24.dp))

                    PrimaryButton(
                        text = if (loading) "Salvando..." else "Criar senha e entrar",
                        onClick = { triggerStep2++ },
                        enabled = canSubmitStep2,
                        loading = loading
                    )

                    Spacer(Modifier.height(14.dp))
                    BackToLogin(onBackToLogin)
                }
            }
        }
    }
}
