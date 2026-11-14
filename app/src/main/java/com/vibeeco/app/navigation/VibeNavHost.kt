package com.vibeeco.app.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import com.composables.icons.lucide.Users
import com.vibeeco.app.data.AppViewModel
import com.vibeeco.app.data.Challenge
import com.vibeeco.app.data.Content
import com.vibeeco.app.data.DesafiosViewModel
import com.vibeeco.app.data.LoginResult
import com.vibeeco.app.ui.auth.*
import com.vibeeco.app.ui.challenges.DesafioDetalheScreen
import com.vibeeco.app.ui.challenges.DesafiosScreen
import com.vibeeco.app.ui.community.FeedScreen
import com.vibeeco.app.ui.home.HomeScreen
import com.vibeeco.app.ui.learn.ArticleDetailScreen
import com.vibeeco.app.ui.learn.LearnScreen
import com.vibeeco.app.ui.profile.ProfileScreen
import com.vibeeco.app.ui.ranking.RankingScreen
import com.vibeeco.app.ui.recompensas.Recompensa
import com.vibeeco.app.ui.recompensas.RecompensasScreen
import com.vibeeco.app.ui.recompensas.Resgate
import com.vibeeco.app.ui.recompensas.ResultadoResgate
import com.vibeeco.app.ui.recompensas.TemaRecompensa
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
    const val DESAFIO = "desafio/{id}"
    const val RANKING = "ranking"
    const val RECOMPENSAS = "recompensas"
    const val LEARN = "learn"
    const val ARTICLE = "article/{id}"
    const val PROFILE = "profile"
    const val COMMUNITY = "community"

    fun verify(email: String) = "verify/${Uri.encode(email)}"
    fun article(id: String) = "article/$id"
    fun desafio(id: String) = "desafio/$id"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(Routes.HOME, "Início", Lucide.House),
    Tab(Routes.CHALLENGES, "Desafios", Lucide.Target),
    Tab(Routes.RANKING, "Ranking", Lucide.Trophy),
    Tab(Routes.COMMUNITY, "Feed", Lucide.Users),
    Tab(Routes.LEARN, "Aprender", Lucide.BookOpen),
    Tab(Routes.PROFILE, "Perfil", Lucide.User),

)

private const val DAILY_GOAL = 3

// Catálogo de exemplo até as recompensas virem do AppViewModel/Store
private val catalogoRecompensas = listOf(
    Recompensa("r1", "Muda de árvore nativa", "Físico", 120, 1, emoji = "🌱"),
    Recompensa("r2", "Ecobag VibeEco", "Físico", 300, 3, estoque = 5, emoji = "👜", tema = TemaRecompensa.Laranja),
    Recompensa("r3", "Curso de compostagem", "Digital", 500, 8, emoji = "📚", tema = TemaRecompensa.Azul),
    Recompensa("r4", "Garrafa reutilizável", "Físico", 250, 2, estoque = 0, emoji = "🥤", tema = TemaRecompensa.Terra),
)

private const val ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

private fun gerarCodigoResgate(): String =
    "VIBE-" + (1..6).map { ALFABETO_CODIGO.random() }.joinToString("")

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
    val desafiosVm: DesafiosViewModel = viewModel()
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
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            AnimatedVisibility(
                visible = tabs.any { it.route == route },
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            ) {
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
                        nav.navigate(Routes.HOME) {
                            popUpTo(nav.graph.findStartDestination().id) { inclusive = true }
                        }
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
                DesafiosScreen(
                    desafios = desafiosVm.desafios,
                    onOpen = { nav.navigate(Routes.desafio(it.id)) },
                    onParticipar = {
                        desafiosVm.participar(it.id)
                        nav.navigate(Routes.desafio(it.id))
                    },
                    onAbandonar = { desafiosVm.abandonar(it.id) },
                )
            }

            composable(
                Routes.DESAFIO,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id").orEmpty()
                val desafio = desafiosVm.buscar(id)
                if (desafio != null) {
                    DesafioDetalheScreen(
                        desafio = desafio,
                        onBack = { nav.popBackStack() },
                        onParticipar = { desafiosVm.participar(id) },
                        onAbandonar = { desafiosVm.abandonar(id) },
                        onToggleTarefa = { desafiosVm.alternarTarefa(id, it.id) },
                    )
                }
            }

            composable(Routes.RANKING) {
                RankingScreen(
                    entries = vm.ranking(),
                    meuNome = vm.user?.name,
                    onAbrirRecompensas = { nav.navigate(Routes.RECOMPENSAS) },
                )
            }

            composable(Routes.RECOMPENSAS) {
                // Estado local temporário: gasto e resgates ficam só em memória
                val resgates = remember { mutableStateListOf<Resgate>() }
                val gasto = resgates.sumOf { it.preco }
                val saldo = (vm.progress.points - gasto).coerceAtLeast(0)
                val nivel = vm.progress.points / 100 + 1 // ajuste se já tiver nível

                RecompensasScreen(
                    saldo = saldo,
                    totalResgatado = gasto,
                    nivelUsuario = nivel,
                    recompensas = catalogoRecompensas,
                    resgates = resgates,
                    onVoltar = {
                        // volta para o ranking; se ele não estiver na pilha, abre a aba
                        if (!nav.popBackStack(Routes.RANKING, false)) nav.goToTab(Routes.RANKING)
                    },
                    onResgatar = { id ->
                        val r = catalogoRecompensas.firstOrNull { it.id == id }
                        when {
                            r == null -> ResultadoResgate.Erro("Recompensa não encontrada.")
                            saldo < r.preco -> ResultadoResgate.Erro("Saldo insuficiente.")
                            else -> {
                                val novo = Resgate(
                                    id = "res-${System.currentTimeMillis()}",
                                    nome = r.titulo,
                                    tipo = r.tipo,
                                    dataIso = java.time.LocalDate.now().toString(),
                                    preco = r.preco,
                                    codigo = gerarCodigoResgate(),
                                    emoji = r.emoji,
                                    tema = r.tema,
                                )
                                resgates.add(0, novo)
                                ResultadoResgate.Ok(novo)
                            }
                        }
                    },
                )
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

            composable(Routes.COMMUNITY) {
                FeedScreen(
                    usuarioNome = vm.user?.name ?: "Visitante",
                    usuarioNivel = "Nível ${vm.progress.points / 100 + 1}", // ajuste se já tiver nível
                )
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