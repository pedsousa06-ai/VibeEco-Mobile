package com.vibeeco.app.ui.auth

import androidx.compose.ui.graphics.Color
import com.vibeeco.app.ui.theme.VibeColors

data class PasswordRules(
    val minLength: Boolean,
    val hasUpper: Boolean,
    val hasDigit: Boolean,
    val hasSpecial: Boolean,
) {
    val score get() = listOf(minLength, hasUpper, hasDigit, hasSpecial).count { it }
    val isValid get() = score == 4
    val label get() = when (score) { 0, 1 -> "Fraca"; 2 -> "Média"; 3 -> "Boa"; else -> "Forte" }
    val color: Color get() = when (score) {
        0, 1 -> VibeColors.Error
        2 -> VibeColors.Warning
        3 -> VibeColors.Medium
        else -> VibeColors.Success
    }
}

fun validatePassword(p: String) = PasswordRules(
    minLength = p.length >= 8,
    hasUpper = p.any { it.isUpperCase() },
    hasDigit = p.any { it.isDigit() },
    hasSpecial = p.any { !it.isLetterOrDigit() && !it.isWhitespace() },
)

fun isValidEmail(email: String) =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()