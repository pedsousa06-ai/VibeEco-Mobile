package com.vibeeco.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vibeeco.app.ui.components.*

enum class PasswordMode(val requiresCurrent: Boolean) {
    FirstAccess(true),   // "Crie uma nova senha" (senha provisória)
    Reset(false),        // após o código do e-mail
    Change(true),        // alterar senha pelo perfil
}

@Composable
fun NewPasswordScreen(
    mode: PasswordMode,
    /** Retorna null se salvou, ou a mensagem de erro. */
    onSave: suspend (current: String?, new: String) -> String?,
    onSaved: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    var current by remember { mutableStateOf("") }
    var new by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var trigger by remember { mutableIntStateOf(0) }

    val rules = validatePassword(new)
    val matches = new == confirm
    val canSave = rules.isValid && confirm.isNotEmpty() && matches &&
        (!mode.requiresCurrent || current.isNotBlank())

    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        loading = true
        val result = onSave(if (mode.requiresCurrent) current else null, new)
        loading = false
        if (result == null) onSaved() else error = result
    }

    AuthScaffold(
        title = when (mode) {
            PasswordMode.FirstAccess -> "Crie uma nova senha"
            PasswordMode.Reset -> "Redefinir senha"
            PasswordMode.Change -> "Alterar senha"
        },
        subtitle = when (mode) {
            PasswordMode.FirstAccess -> "Esta será sua senha para acessar o VibeEco de agora em diante."
            PasswordMode.Reset -> "Código confirmado! Escolha uma nova senha para sua conta."
            PasswordMode.Change -> "Informe sua senha atual e escolha uma nova."
        },
    ) {
        if (mode.requiresCurrent) {
            VibeTextField(
                if (mode == PasswordMode.FirstAccess) "Senha Atual (provisória)" else "Senha Atual",
                current, { current = it; error = null },
                placeholder = if (mode == PasswordMode.FirstAccess) "Digite sua senha provisória" else "Digite sua senha atual",
                isPassword = true,
            )
            Spacer(Modifier.height(14.dp))
        }
        VibeTextField("Nova Senha", new, { new = it; error = null },
            placeholder = "Digite sua nova senha", isPassword = true)
        PasswordStrengthIndicator(rules, visible = new.isNotEmpty())
        Spacer(Modifier.height(14.dp))
        VibeTextField("Confirmar Nova Senha", confirm, { confirm = it; error = null },
            placeholder = "Confirme sua nova senha", isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = { if (canSave) trigger++ },
            error = error ?: if (confirm.isNotEmpty() && !matches) "As senhas não coincidem" else null)
        PasswordRequirements(rules)
        Spacer(Modifier.height(28.dp))
        PrimaryButton("Salvar nova senha", onClick = { error = null; trigger++ },
            enabled = canSave, loading = loading)
        if (onBack != null) {
            Spacer(Modifier.height(8.dp))
            BackToLogin(onBack, "← Voltar")
        }
    }
}
