package com.vibeeco.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibeeco.app.R
import com.vibeeco.app.ui.auth.PasswordRules
import com.vibeeco.app.ui.theme.*

/** Fundo de folhas com filtro escurecido idêntico ao desktop AuthLayout.vue */
@Composable
fun VibeBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(ForestDarkest)
    ) {
        // Tenta carregar fundo_cortado ou bg_vibeeco
        Image(
            painter = painterResource(R.drawable.fundo_cortado),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Overlay verde-escuro translúcido linear-gradient(rgba(0, 25, 10, 0.45), rgba(0, 25, 10, 0.45))
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0x7300190A))
        )
        content()
    }
}

/** Scaffold compartilhado para telas de autenticação: Logo + Cartão Branco flutuante */
@Composable
fun AuthScaffold(
    title: String,
    subtitle: String,
    topAction: (@Composable () -> Unit)? = null,
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
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(16.dp))

            // Logo Principal
            VibeEcoLogo(
                modifier = Modifier
                    .widthIn(max = 240.dp)
                    .padding(vertical = 12.dp)
            )

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visibleState = visible,
                enter = fadeIn(tween(450)) + slideInVertically(tween(450)) { it / 8 },
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 480.dp),
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 28.dp)
                    ) {
                        if (topAction != null) {
                            topAction()
                            Spacer(Modifier.height(12.dp))
                        }

                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF203B27),
                                fontSize = 24.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF829084),
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp)
                        )

                        Spacer(Modifier.height(24.dp))

                        content()
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

/** Logo do VibeEco usando a imagem oficial logo_principal ou fallback */
@Composable
fun VibeEcoLogo(
    modifier: Modifier = Modifier,
    textSize: Int = 32,
    iconSize: Int = 40
) {
    Image(
        painter = painterResource(R.drawable.logo_principal),
        contentDescription = "VibeEco",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .fillMaxWidth(0.65f)
            .heightIn(max = 64.dp)
    )
}

/** Campo de texto estilizado idêntico aos inputs do web front-end */
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

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF344638),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF829084),
                    fontSize = 13.5.sp
                )
            },
            shape = RoundedCornerShape(10.dp),
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
                {
                    TextButton(
                        onClick = { visible = !visible },
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text(
                            text = if (visible) "Ocultar" else "Mostrar",
                            color = GreenBrand,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderInput,
                focusedBorderColor = GreenBrandLight,
                errorBorderColor = RedDanger,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedTextColor = TextMain,
                unfocusedTextColor = TextMain,
                errorTextColor = TextMain,
                disabledTextColor = TextSubtle,
                cursorColor = GreenBrand,
                errorCursorColor = GreenBrand,
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = TextMain
            )
        )

        if (error != null) {
            Text(
                text = error,
                color = RedDangerDark,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 2.dp)
            )
        }
    }
}

/** Botão Principal com degradê verde esmeralda idêntico ao desktop */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val shape = RoundedCornerShape(10.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(
                elevation = if (enabled && !loading) 4.dp else 0.dp,
                shape = shape,
                ambientColor = GreenBrand.copy(alpha = 0.35f),
                spotColor = GreenBrand.copy(alpha = 0.35f)
            )
            .clip(shape)
            .background(
                brush = if (enabled && !loading) {
                    Brush.horizontalGradient(listOf(Color(0xFF1A8F4E), Color(0xFF22C55E)))
                } else {
                    SolidColor(GreenBrand.copy(alpha = 0.45f))
                }
            )
            .clickable(
                enabled = enabled && !loading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                letterSpacing = 0.2.sp
            )
        }
    }
}

/** Link elegante para retornar ao Login com seta */
@Composable
fun BackToLogin(onClick: () -> Unit, text: String = "← Voltar ao login") {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = Color(0xFF5B7A66),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/** Banner de Mensagem de Aviso / Sucesso / Erro idêntico ao web */
@Composable
fun VibeAlert(
    message: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isError) Color(0xFFFDECEC) else Color(0xFFE3F4E8)
    val textColor = if (isError) Color(0xFFC62828) else Color(0xFF1B7A34)

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
    ) {
        Text(
            text = message,
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}

/** 4 barrinhas com status e cores dinâmicas (Fraca / Média / Boa / Forte) */
@Composable
fun PasswordStrengthIndicator(rules: PasswordRules, visible: Boolean) {
    AnimatedVisibility(visible) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(4) { i ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                color = if (i < rules.score) rules.color else BorderInput.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = rules.label,
                color = rules.color,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

/** Requisitos de senha com checklist */
@Composable
fun PasswordRequirements(rules: PasswordRules) {
    Column(Modifier.padding(top = 10.dp)) {
        Text(
            text = "Sua senha deve conter:",
            color = GreenBrand,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.5.sp
        )
        Spacer(Modifier.height(4.dp))
        RequirementRow("Mínimo de 8 caracteres", rules.minLength)
        RequirementRow("Uma letra maiúscula", rules.hasUpper)
        RequirementRow("Um número", rules.hasDigit)
        RequirementRow("Um caractere especial (@, $, %, &, #)", rules.hasSpecial)
    }
}

@Composable
private fun RequirementRow(text: String, ok: Boolean) {
    Row(
        Modifier.padding(start = 6.dp, top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (ok) "✓" else "•",
            color = if (ok) GreenBrand else TextSubtle,
            fontWeight = if (ok) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.5.sp
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            color = if (ok) GreenOnContainer else TextSubtle,
            fontSize = 12.5.sp
        )
    }
}

/** Campo de código OTP de 6 dígitos com caixinhas modernas */
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
        cursorBrush = SolidColor(GreenBrand),
        decorationBox = {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(length) { i ->
                    val active = i == value.length
                    val char = value.getOrNull(i)?.toString() ?: ""
                    Box(
                        Modifier
                            .weight(1f)
                            .aspectRatio(0.85f)
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .border(
                                width = if (active) 2.dp else 1.dp,
                                color = when {
                                    isError -> RedDanger
                                    active -> GreenBrandLight
                                    char.isNotEmpty() -> GreenBrand
                                    else -> BorderInput
                                },
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = char,
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                        )
                    }
                }
            }
        },
    )
}
