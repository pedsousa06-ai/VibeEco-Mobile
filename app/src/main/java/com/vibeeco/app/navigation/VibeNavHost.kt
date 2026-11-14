package com.vibeeco.app.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Target
import com.composables.icons.lucide.Trophy
import com.composables.icons.lucide.User
import com.vibeeco.app.data.AppViewModel
import com.vibeeco.app.data.Challenge
import com.vibeeco.app.data.Content
import com.vibeeco.app.data.LoginResult
import com.vibeeco.app.ui.auth.*
import com.vibeeco.app.ui.challenges.ChallengesScreen
import com.vibeeco.app.ui.home.HomeScreen
import com.vibeeco.app.ui.learn.ArticleDetailScreen
import com.vibeeco.app.ui.learn.LearnScreen
import com.vibeeco.app.ui.profile.ProfileScreen
import com.vibeeco.app.ui.ranking.RankingScreen
import com.vibeeco.app.ui.theme.BorderLight
import com.vibeeco.app.ui.theme.GreenBrand
import com.vibeeco.app.ui.theme.GreenContainer
import com.vibeeco.app.ui.theme.TextMuted
import kotlinx.coroutines.delay

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val FORGOT = "forgot"
    const val VERIFY = "verify/{email}"
    const val RESET = "reset"
    const val FIRST_ACCESS = "first_access"
    const val CHANGE_PASSWORD = "change_password"
    const val HOME = "home"
    const val CHALLENGES = "challenges"
    const val RANKING = "ranking"
    const val LEARN = "learn"
    const val ARTICLE = "article/{id}"
    const val PROFILE = "profile"

    fun verify(email: String) = "verify/${Uri.encode(email)}"
    fun article(id: String) = "article/$id"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(Routes.HOME, "Início", Lucide.House),
    Tab(Routes.CHALLENGES, "Desafios", Lucide.Target),
    Tab(Routes.RANKING, "Ranking", Lucide.Trophy),
    Tab(Routes.LEARN, "Aprender", Lucide.BookOpen),
    Tab(Routes.PROFILE, "Perfil", Lucide.User),
)

private const val DAILY_GOAL = 3

private fun NavHostController.goToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavHostController.goToLogin() {
    navigate(Routes.LOGIN) {
        popUpTo(graph.findStartDestination().id) { inclusive = true }
    }
}

@Composable
fun VibeNavHost() {
    val vm: AppViewModel = viewModel()
    val nav = rememberNavController()
    val context = LocalContext.current
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route

    fun toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    val onToggleChallenge: (Challenge) -> Unit = { c ->
        if (vm.toggleChallenge(c)) toast("+${c.points} pontos!")
    }

    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AnimatedVisibility(visible = tabs.any { it.route == route }) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    modifier = Modifier.drawBehind {
                        drawLine(
                            color = BorderLight,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                ) {
                    tabs.forEach { tab ->
                        val isSelected = route == tab.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { if (route != tab.route) nav.goToTab(tab.route) },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = {
                                Text(
                                    tab.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GreenBrand,
                                selectedTextColor = GreenBrand,
                                indicatorColor = GreenContainer,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                            ),
                        )
                    }
                }
            }
        },
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(bottom = inner.calculateBottomPadding()),
        ) {

            composable(Routes.SPLASH) {
                SplashScreen(onFinished = {
                    val u = vm.user
                    val dest = when {
                        !vm.onboardingSeen -> Routes.ONBOARDING
                        u != null && !u.mustChangePassword -> Routes.HOME
                        else -> Routes.LOGIN
                    }
                    nav.navigate(dest) { popUpTo(Routes.SPLASH) { inclusive = true } }
                })
            }

            composable(Routes.ONBOARDING) {
                OnboardingScreen(onFinish = {
                    vm.finishOnboarding()
                    nav.navigate(Routes.LOGIN) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                })
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onLogin = { id, password, remember ->
                        delay(600)
                        when (val result = vm.login(id, password, remember)) {
                            LoginResult.Success -> {
                                nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                                null
                            }
                            LoginResult.FirstAccess -> {
                                nav.navigate(Routes.FIRST_ACCESS)
                                null
                            }
                            is LoginResult.Error -> result.message
                        }
                    },
                    onForgotPassword = { nav.navigate(Routes.FORGOT) },
                    onFirstAccess = { nav.navigate(Routes.FIRST_ACCESS) },
                )
            }

            composable(Routes.FORGOT) {
                ForgotPasswordScreen(
                    onSendLink = { email ->
                        delay(900)
                        if (vm.startRecovery(email)) null else "E-mail não encontrado na instituição."
                    },
                    onContinueToCode = { email -> nav.navigate(Routes.verify(email)) },
                    onBackToLogin = { nav.popBackStack(Routes.LOGIN, false) },
                )
            }

            composable(
                Routes.VERIFY,
                arguments = listOf(navArgument("email") { type = NavType.StringType }),
            ) { backStackEntry ->
                VerifyCodeScreen(
                    email = backStackEntry.arguments?.getString("email").orEmpty(),
                    demoCode = vm.pendingCode,
                    onVerify = { _, code -> delay(600); vm.verifyCode(code) },
                    onResend = { vm.resendCode() },
                    onVerified = {
                        nav.navigate(Routes.RESET) { popUpTo(Routes.FORGOT) { inclusive = true } }
                    },
                    onBack = { nav.popBackStack() },
                )
            }

            composable(Routes.RESET) {
                NewPasswordScreen(
                    mode = PasswordMode.Reset,
                    onSave = { _, new ->
                        delay(700)
                        if (vm.resetPassword(new)) null else "Sessão expirada. Refaça a recuperação."
                    },
                    onSaved = {
                        toast("Senha redefinida! Faça login com a nova senha.")
                        nav.goToLogin()
                    },
                )
            }

            composable(Routes.FIRST_ACCESS) {
                FirstAccessScreen(
                    initialEmail = vm.user?.email.orEmpty(),
                    onVerifyUser = { id ->
                        delay(400)
                        vm.verifyFirstAccessUser(id)
                    },
                    onComplete = { id, current, new ->
                        delay(600)
                        vm.completeFirstAccess(id, current, new)
                    },
                    onSuccess = {
                        toast("Senha criada! Bem-vindo ao VibeEco")
                        nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onBackToLogin = { nav.popBackStack(Routes.LOGIN, false) },
                )
            }

            composable(Routes.CHANGE_PASSWORD) {
                NewPasswordScreen(
                    mode = PasswordMode.Change,
                    onSave = { current, new -> delay(700); vm.changePassword(current, new) },
                    onSaved = {
                        toast("Senha alterada com sucesso!")
                        nav.popBackStack()
                    },
                    onBack = { nav.popBackStack() },
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    userName = vm.user?.name ?: "Visitante",
                    points = vm.progress.points,
                    streak = vm.progress.streak,
                    co2Grams = vm.progress.co2Grams,
                    doneIds = vm.progress.doneIds,
                    dailyGoal = DAILY_GOAL,
                    featured = Content.challenges.take(3),
                    tip = Content.tips.random(),
                    onToggle = onToggleChallenge,
                    onSeeAll = { nav.goToTab(Routes.CHALLENGES) },
                    onOpenProfile = { nav.goToTab(Routes.PROFILE) },
                    onOpenLearn = { nav.goToTab(Routes.LEARN) },
                )
            }

            composable(Routes.CHALLENGES) {
                ChallengesScreen(Content.challenges, vm.progress.doneIds, onToggleChallenge)
            }

            composable(Routes.RANKING) {
                RankingScreen(vm.ranking())
            }

            composable(Routes.LEARN) {
                LearnScreen(
                    articles = Content.articles,
                    readIds = vm.progress.readArticles,
                    onOpen = { nav.navigate(Routes.article(it.id)) },
                )
            }

            composable(
                Routes.ARTICLE,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                val article = Content.articles.firstOrNull { it.id == id }
                if (article != null) {
                    ArticleDetailScreen(
                        article = article,
                        isRead = article.id in vm.progress.readArticles,
                        onBack = { nav.popBackStack() },
                        onMarkRead = {
                            if (vm.markArticleRead(article)) toast("+${article.points} pontos por aprender!")
                        },
                    )
                }
            }

            composable(Routes.PROFILE) {
                val u = vm.user
                if (u != null) {
                    ProfileScreen(
                        user = u,
                        progress = vm.progress,
                        notifications = vm.notifications,
                        onNotificationsChange = { vm.updateNotifications(it) },
                        onChangePassword = { nav.navigate(Routes.CHANGE_PASSWORD) },
                        onLogout = {
                            vm.logout()
                            nav.goToLogin()
                        },
                    )
                }
            }
        }
    }
}