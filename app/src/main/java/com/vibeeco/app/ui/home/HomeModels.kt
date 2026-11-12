package com.vibeeco.app.ui.home

/** Modelos da tela de Início. Hoje usam dados simulados (HomeMock); depois é só preencher com a API. */

data class HomeUser(
    val name: String,
    val coins: Int,
    val streakDays: Int,
    val bestStreak: Int,
    val level: Int,
    val levelName: String,
    val xp: Int,
    val xpNextLevel: Int,
    val xpToday: Int,
    val rankingPosition: Int,
    val rankingTopPercent: Int,
    val missionsCompleted: Int,
    val missionsAvailable: Int,
)

data class HomeMission(
    val id: String,
    val category: String,
    val difficulty: String,
    val title: String,
    val xp: Int,
    val deadline: String,
    val progress: Int,          // 0..100
)

data class HomeChallenge(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val collective: Boolean,
    val difficulty: String?,    // null = não mostra a etiqueta
    val progress: Int,          // 0..100
    val xp: Int,
    val coins: Int,
    val participants: Int,
    val daysLeft: Int,          // <= 1 mostra alerta de urgência
    val rewardXp: Int,
)

data class HomeContent(
    val id: String,
    val category: String,
    val title: String,
    val readMinutes: Int,
    val xp: Int,
    val done: Boolean,
)

enum class Tone { Green, Blue, Yellow, Purple, Orange }

data class HomeAchievement(val id: String, val emoji: String, val name: String, val tone: Tone)

data class HomeUiState(
    val user: HomeUser,
    val missions: List<HomeMission>,
    val challenges: List<HomeChallenge>,
    val contents: List<HomeContent>,
    val achievements: List<HomeAchievement>,
)

object HomeMock {
    val state = HomeUiState(
        user = HomeUser(
            name = "Ana", coins = 620, streakDays = 12, bestStreak = 21,
            level = 14, levelName = "Guardião Verde", xp = 3840, xpNextLevel = 4500, xpToday = 150,
            rankingPosition = 12, rankingTopPercent = 5,
            missionsCompleted = 47, missionsAvailable = 63,
        ),
        missions = listOf(
            HomeMission("m1", "Reciclagem", "Fácil", "Separar o lixo reciclável por uma semana", 150, "3 dias", 70),
            HomeMission("m2", "Energia", "Médio", "Semana do consumo consciente de energia", 220, "5 dias", 45),
            HomeMission("m3", "Transporte", "Difícil", "Desafio da mobilidade sustentável", 350, "6 dias", 20),
        ),
        challenges = listOf(
            HomeChallenge(
                "d1", "♻️", "Descarte Consciente", "Separe corretamente 3 tipos de resíduos.",
                collective = false, difficulty = "Fácil", progress = 70, xp = 150, coins = 20,
                participants = 0, daysLeft = 7, rewardXp = 300,
            ),
            HomeChallenge(
                "d2", "💧", "Desafio da Água", "Reduza 20% do consumo em 7 dias.",
                collective = true, difficulty = null, progress = 38, xp = 300, coins = 0,
                participants = 1240, daysLeft = 5, rewardXp = 300,
            ),
        ),
        contents = listOf(
            HomeContent("a1", "Pegada de carbono", "O que é pegada de carbono e como reduzir a sua", 5, 80, false),
            HomeContent("a2", "Reciclagem", "Reciclagem na prática: o que vai em cada lixeira", 4, 60, false),
            HomeContent("a3", "Energia", "Energia limpa: por que ela importa para o campus", 6, 70, true),
        ),
        achievements = listOf(
            HomeAchievement("c1", "🌱", "Primeira Missão", Tone.Green),
            HomeAchievement("c2", "♻️", "Reciclador", Tone.Green),
            HomeAchievement("c3", "💧", "Guardião da Água", Tone.Blue),
            HomeAchievement("c4", "⚡", "Eco Energético", Tone.Yellow),
        ),
    )
}
