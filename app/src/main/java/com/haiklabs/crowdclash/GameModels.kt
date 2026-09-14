package com.haiklabs.crowdclash

data class SurveyQuestion(val prompt: String, val answers: List<SurveyAnswer>)
data class SurveyAnswer(val text: String, val points: Int)

data class GameState(
    val roomCode: String = "",
    val round: Int = 0,
    val revealed: Set<Int> = emptySet(),
    val teamAScore: Int = 0,
    val teamBScore: Int = 0,
    val activeTeam: Int = 0,
    val strikes: Int = 0,
    val buzzerTeam: Int? = null,
    val revision: Long = 0
)

enum class Role { HOST, PLAYER }

