package com.vibeeco.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.R
import com.vibeeco.app.ui.auth.PasswordRules
import com.vibeeco.app.ui.theme.Colors

/** Fundo de folhas do Figma com um leve degradê para dar leitura ao conteúdo. */
@Composable
fun VibeBackground(content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(Color(0xFF0F2A1B))) {
        Image(
            painter = painterResource(R.drawable.bg_vibeeco),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0x33000000), Color.Transparent, Color(0x55000000)))
            )
        )
        content()
    }
}

/** Logo + cartão branco (layout comum às telas de autenticação). */
@Composable
fun AuthScaffold(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val visible = remember { MutableTransitionState(false).apply { targetState = true } }
    VibeBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(56.dp))
            VibeEcoLogo()
            Spacer(Modifier.height(28.dp))
            AnimatedVisibility(
                visibleState = visible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 6 },
            ) {
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(horizontal = 18.dp, vertical = 28.dp)) {
                        Text(title, Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(6.dp))
                        Text(subtitle, Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(28.dp))
                        content()
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun VibeEcoLogo(modifier: Modifier = Modifier, textSize: Int = 34, iconSize: Int = 44) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.logo_leaf),
            contentDescription = null,
            modifier = Modifier.size(iconSize.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) { append("Vibe") }
                withStyle(SpanStyle(color = Colors.GreenBright)) { append("Eco") }
            },
            fontSize = textSize.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun VibeTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: (() -> Unit)? = null,
) {
    var visible by remember { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null,
            placeholder = { Text(placeholder, color = Colors.Muted, fontSize = 14.sp) },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
                imeAction = imeAction,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focus.moveFocus(FocusDirection.Down) },
                onDone = { focus.clearFocus(); onImeAction?.invoke() },
            ),
            visualTransformation = if (isPassword && !visible) PasswordVisualTransformation()
                                   else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                { TextButton(onClick = { visible = !visible }) {
                    Text(if (visible) "Ocultar" else "Mostrar", color = Colors.Green, fontSize = 12.sp)
                } }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Colors.Border,
                focusedBorderColor = Colors.Green,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
        )
        if (error != null) {
            Text(error, color = Colors.Error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, enabled: Boolean = true, loading: Boolean = false) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.Green,
            disabledContainerColor = Colors.Green.copy(alpha = 0.45f),
            disabledContentColor = Color.White,
        ),
    ) {
        if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        else Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun BackToLogin(onClick: () -> Unit, text: String = "← Voltar ao login") {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(text, color = Colors.Muted, fontSize = 15.sp)
    }
}

/** 4 barrinhas + rótulo (Fraca/Média/Boa/Forte) como no Figma. */
@Composable
fun PasswordStrengthIndicator(rules: PasswordRules, visible: Boolean) {
    AnimatedVisibility(visible) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(4) { i ->
                    Box(
                        Modifier.weight(1f).height(4.dp)
                            .background(
                                if (i < rules.score) rules.color else Colors.Border.copy(alpha = 0.5f),
                                RoundedCornerShape(2.dp))
                    )
                }
            }
            Spacer(Modifier.width(24.dp))
            Text(rules.label, color = rules.color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun PasswordRequirements(rules: PasswordRules) {
    Column(Modifier.padding(top = 12.dp)) {
        Text("Sua senha deve conter:", color = Colors.Green, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        RequirementRow("Mínimo de 8 caracteres", rules.minLength)
        RequirementRow("Uma letra maiúscula", rules.hasUpper)
        RequirementRow("Um número", rules.hasDigit)
        RequirementRow("Um caractere especial (@, $, %, &, #)", rules.hasSpecial)
    }
}

@Composable
private fun RequirementRow(text: String, ok: Boolean) {
    Row(Modifier.padding(start = 8.dp, top = 2.dp)) {
        Text(if (ok) "✓" else "•", color = if (ok) Colors.Success else Colors.Muted, fontSize = 13.sp)
        Spacer(Modifier.width(8.dp))
        Text(text, color = if (ok) Colors.Success else Colors.Muted, fontSize = 13.sp)
    }
}

/** Campo de código OTP (6 dígitos) com caixinhas. */
@Composable
fun OtpCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 6,
    isError: Boolean = false,
) {
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    BasicTextField(
        value = value,
        onValueChange = { if (it.length <= length && it.all(Char::isDigit)) onValueChange(it) },
        modifier = Modifier.focusRequester(focus),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        cursorBrush = SolidColor(Colors.Green),
        decorationBox = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(length) { i ->
                    val active = i == value.length
                    Box(
                        Modifier.weight(1f).aspectRatio(0.85f)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(
                                width = if (active) 2.dp else 1.dp,
                                color = when {
                                    isError -> Colors.Error
                                    active -> Colors.Green
                                    else -> Colors.Border
                                },
                                shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(value.getOrNull(i)?.toString() ?: "",
                            style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Colors.Title))
                    }
                }
            }
        },
    )
}
