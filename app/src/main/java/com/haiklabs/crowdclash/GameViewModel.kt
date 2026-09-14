package com.haiklabs.crowdclash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var role by mutableStateOf<Role?>(null); private set
    var game by mutableStateOf(GameState()); private set
    var connected by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set
    var playerTeam by mutableStateOf(0); private set

    fun host() {
        role = Role.HOST
        game = GameState(roomCode = buildString { repeat(6) { append("ABCDEFGHJKLMNPQRSTUVWXYZ23456789".random()) } })
        connected = true
        sync(game)
        startPolling()
    }

    fun join(code: String, team: Int) {
        val clean = code.uppercase().filter(Char::isLetterOrDigit).take(6)
        if (clean.length != 6) { error = "Enter the full 6-character room code"; return }
        role = Role.PLAYER; playerTeam = team; game = GameState(roomCode = clean); error = null
        startPolling()
    }

    fun leave() { role = null; game = GameState(); connected = false; error = null }

    fun reveal(index: Int) = update { state ->
        if (index in state.revealed) state else {
            val points = Questions.all[state.round % Questions.all.size].answers[index].points
            state.copy(revealed = state.revealed + index,
                teamAScore = state.teamAScore + if (state.activeTeam == 0) points else 0,
                teamBScore = state.teamBScore + if (state.activeTeam == 1) points else 0)
        }
    }
    fun strike() = update { it.copy(strikes = (it.strikes + 1).coerceAtMost(3)) }
    fun switchTeam() = update { it.copy(activeTeam = 1 - it.activeTeam, strikes = 0, buzzerTeam = null) }
    fun nextRound() = update { it.copy(round = (it.round + 1) % Questions.all.size, revealed = emptySet(), strikes = 0, buzzerTeam = null) }
    fun buzz() = update { if (it.buzzerTeam == null) it.copy(buzzerTeam = playerTeam) else it }
    fun clearBuzz() = update { it.copy(buzzerTeam = null) }

    private fun update(block: (GameState) -> GameState) {
        game = block(game).copy(revision = System.currentTimeMillis())
        sync(game)
    }
    private fun sync(state: GameState) = viewModelScope.launch {
        runCatching { RoomWire.publish(state) }.onFailure { error = "Could not sync. Check your connection." }
    }
    private fun startPolling() = viewModelScope.launch {
        while (isActive && role != null) {
            runCatching { RoomWire.latest(game.roomCode) }
                .onSuccess { remote ->
                    if (remote != null && remote.revision >= game.revision) game = remote
                    connected = remote != null || role == Role.HOST
                    if (remote != null) error = null
                }.onFailure { if (!connected) error = "Looking for the room…" }
            delay(1_500)
        }
    }
}

