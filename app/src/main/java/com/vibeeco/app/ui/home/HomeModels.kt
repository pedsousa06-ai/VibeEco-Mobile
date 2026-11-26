package com.vibeeco.app.ui.home

/** Modelos da tela de Início. Preenchidos com os dados da API (telaInicio). */

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
