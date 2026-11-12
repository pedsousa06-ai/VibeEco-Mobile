package com.vibeeco.app.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.vibeeco.app.data.Content
import com.vibeeco.app.data.Progress
import com.vibeeco.app.data.RankEntry
import com.vibeeco.app.data.User
import com.vibeeco.app.ui.challenges.ChallengesScreen
import com.vibeeco.app.ui.home.HomeScreen
import com.vibeeco.app.ui.learn.LearnScreen
import com.vibeeco.app.ui.profile.ProfileScreen
import com.vibeeco.app.ui.ranking.RankingScreen
import com.vibeeco.app.ui.theme.VibeEcoTheme

private const val SPEC = "spec:width=390dp,height=844dp,dpi=420"

@Preview(name = "1 Splash", device = SPEC)
@Composable
private fun SplashPreview() = VibeEcoTheme { SplashScreen(onFinished = {}) }

@Preview(name = "2 Onboarding", device = SPEC)
@Composable
private fun OnboardingPreview() = VibeEcoTheme { OnboardingScreen(onFinish = {}) }

@Preview(name = "3 Login", device = SPEC)
@Composable
private fun LoginPreview() = VibeEcoTheme {
    LoginScreen(onLogin = { _, _, _ -> null }, onForgotPassword = {})
}

@Preview(name = "4 Esqueceu a senha", device = SPEC)
@Composable
private fun ForgotPreview() = VibeEcoTheme {
    ForgotPasswordScreen(onSendLink = { null }, onContinueToCode = {}, onBackToLogin = {})
}

@Preview(name = "5 Verificar código", device = SPEC)
@Composable
private fun VerifyPreview() = VibeEcoTheme {
    VerifyCodeScreen(
        email = "ana.beatriz@ufmg.br", demoCode = null,
        onVerify = { _, _ -> true }, onResend = {}, onVerified = {}, onBack = {},
    )
}

@Preview(name = "6 Redefinir senha", device = SPEC)
@Composable
private fun ResetPreview() = VibeEcoTheme {
    NewPasswordScreen(PasswordMode.Reset, onSave = { _, _ -> null }, onSaved = {})
}

@Preview(name = "7 Primeiro acesso", device = SPEC)
@Composable
private fun FirstAccessPreview() = VibeEcoTheme {
    NewPasswordScreen(PasswordMode.FirstAccess, onSave = { _, _ -> null }, onSaved = {})
}

private val sampleProgress = Progress(
    points = 230, co2Grams = 18400, actions = 14, streak = 3,
    doneIds = setOf("c1"), readArticles = setOf("a1", "a2"),
)
private val sampleUser = User("Ana Beatriz Souza", "ana.beatriz@ufmg.br", "2023001234", false)

@Preview(name = "8 Início", device = SPEC)
@Composable
private fun HomePreview() = VibeEcoTheme {
    HomeScreen(
        userName = sampleUser.name, points = sampleProgress.points, streak = sampleProgress.streak,
        co2Grams = sampleProgress.co2Grams, doneIds = sampleProgress.doneIds, dailyGoal = 3,
        featured = Content.challenges.take(3), tip = Content.tips.first(),
        onToggle = {}, onSeeAll = {}, onOpenProfile = {}, onOpenLearn = {},
    )
}

@Preview(name = "9 Desafios", device = SPEC)
@Composable
private fun ChallengesPreview() = VibeEcoTheme {
    ChallengesScreen(Content.challenges, sampleProgress.doneIds, onToggle = {})
}

@Preview(name = "10 Ranking", device = SPEC)
@Composable
private fun RankingPreview() = VibeEcoTheme {
    RankingScreen(Content.rivals + RankEntry(sampleUser.name, "UFMG", sampleProgress.points, true))
}

@Preview(name = "11 Aprender", device = SPEC)
@Composable
private fun LearnPreview() = VibeEcoTheme {
    LearnScreen(Content.articles, sampleProgress.readArticles, onOpen = {})
}

@Preview(name = "12 Perfil", device = SPEC)
@Composable
private fun ProfilePreview() = VibeEcoTheme {
    ProfileScreen(sampleUser, sampleProgress, true, {}, {}, {})
}
