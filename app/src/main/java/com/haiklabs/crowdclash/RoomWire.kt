package com.haiklabs.crowdclash

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object RoomWire {
    private const val BASE = "https://ntfy.sh/cc_live_"

    fun encode(state: GameState): String = JSONObject().apply {
        put("room", state.roomCode); put("round", state.round)
        put("revealed", state.revealed.sorted().joinToString(","))
        put("a", state.teamAScore); put("b", state.teamBScore)
        put("active", state.activeTeam); put("strikes", state.strikes)
        put("buzz", state.buzzerTeam ?: -1); put("revision", state.revision)
    }.toString()

    fun decode(value: String): GameState? = runCatching {
        val json = JSONObject(value)
        GameState(
            roomCode = json.getString("room"), round = json.getInt("round"),
            revealed = json.getString("revealed").split(',').mapNotNull(String::toIntOrNull).toSet(),
            teamAScore = json.getInt("a"), teamBScore = json.getInt("b"),
            activeTeam = json.getInt("active"), strikes = json.getInt("strikes"),
            buzzerTeam = json.getInt("buzz").takeIf { it >= 0 }, revision = json.getLong("revision")
        )
    }.getOrNull()

    suspend fun publish(state: GameState) = withContext(Dispatchers.IO) {
        val connection = URL(BASE + state.roomCode.lowercase()).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8")
        connection.outputStream.use { it.write(encode(state).toByteArray()) }
        check(connection.responseCode in 200..299) { "Room service returned ${connection.responseCode}" }
        connection.disconnect()
    }

    suspend fun latest(roomCode: String): GameState? = withContext(Dispatchers.IO) {
        val connection = URL("${BASE}${roomCode.lowercase()}/json?poll=1").openConnection() as HttpURLConnection
        connection.connectTimeout = 8_000; connection.readTimeout = 8_000
        val lines = if (connection.responseCode in 200..299) connection.inputStream.bufferedReader().readLines() else emptyList()
        connection.disconnect()
        lines.asReversed().firstNotNullOfOrNull { line ->
            runCatching { JSONObject(line).optString("message") }.getOrNull()?.let(::decode)
        }
    }
}

