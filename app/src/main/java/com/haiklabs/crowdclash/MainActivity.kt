package com.haiklabs.crowdclash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val Navy = Color(0xFF11142B)
private val Purple = Color(0xFF7357FF)
private val Coral = Color(0xFFFF6B5E)
private val Cream = Color(0xFFFFF8E8)
private val Mint = Color(0xFF57D6B0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = darkColorScheme(primary = Purple, secondary = Coral, background = Navy, surface = Color(0xFF1C2040))) { CrowdClash() } }
    }
}

@Composable private fun CrowdClash(vm: GameViewModel = viewModel()) {
    Surface(Modifier.fillMaxSize(), color = Navy) {
        when (vm.role) {
            null -> Welcome(vm)
            else -> GameRoom(vm)
        }
    }
}

@Composable private fun Welcome(vm: GameViewModel) {
    var joining by remember { mutableStateOf(false) }
    var code by remember { mutableStateOf("") }
    var team by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(.5f))
        Text("● ● ●", color = Mint, letterSpacing = 8.sp)
        Text("CROWD", fontSize = 54.sp, lineHeight = 54.sp, fontWeight = FontWeight.Black, color = Cream)
        Text("CLASH", fontSize = 54.sp, lineHeight = 54.sp, fontWeight = FontWeight.Black, color = Coral)
        Text("Guess the crowd. Win the room.", color = Cream.copy(alpha = .72f), fontSize = 17.sp)
        Spacer(Modifier.height(44.dp))
        if (!joining) {
            BigButton("HOST A GAME", Purple) { vm.host() }
            Spacer(Modifier.height(12.dp))
            BigButton("JOIN A ROOM", Color(0xFF292E55)) { joining = true }
        } else {
            OutlinedTextField(code, { code = it.uppercase().filter(Char::isLetterOrDigit).take(6) }, label = { Text("ROOM CODE") },
                textStyle = LocalTextStyle.current.copy(fontSize = 28.sp, letterSpacing = 6.sp, textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TeamChoice("TEAM SUN", Coral, team == 0, Modifier.weight(1f)) { team = 0 }
                TeamChoice("TEAM MOON", Purple, team == 1, Modifier.weight(1f)) { team = 1 }
            }
            Spacer(Modifier.height(16.dp)); BigButton("LET'S PLAY", Mint) { vm.join(code, team) }
            TextButton({ joining = false }) { Text("Back") }
        }
        vm.error?.let { Text(it, color = Coral, textAlign = TextAlign.Center) }
        Spacer(Modifier.weight(1f))
        Text("No accounts • 2 teams • 5-minute rounds", color = Cream.copy(alpha = .48f), fontSize = 12.sp)
    }
}

@Composable private fun GameRoom(vm: GameViewModel) {
    val state = vm.game
    val question = Questions.all[state.round % Questions.all.size]
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(vm::leave) { Text("‹ EXIT") }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ROOM", fontSize = 10.sp, color = Cream.copy(alpha = .6f))
                Text(state.roomCode, fontWeight = FontWeight.Black, letterSpacing = 3.sp, color = Mint)
            }
            Spacer(Modifier.weight(1f)); Text(if (vm.connected) "● LIVE" else "○ SYNC", color = if (vm.connected) Mint else Coral, fontSize = 11.sp)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ScoreCard("TEAM SUN", state.teamAScore, Coral, state.activeTeam == 0, Modifier.weight(1f))
            ScoreCard("TEAM MOON", state.teamBScore, Purple, state.activeTeam == 1, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Text("ROUND ${state.round + 1}", color = Mint, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(question.prompt, color = Cream, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 29.sp)
        Spacer(Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            question.answers.forEachIndexed { index, answer ->
                val shown = index in state.revealed
                Row(Modifier.fillMaxWidth().background(if (shown) Cream else Color(0xFF252A50), RoundedCornerShape(12.dp))
                    .clickable(enabled = vm.role == Role.HOST) { vm.reveal(index) }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${index + 1}", color = if (shown) Navy.copy(alpha = .5f) else Cream.copy(alpha = .5f), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(14.dp)); Text(if (shown) answer.text else "TAP TO REVEAL", Modifier.weight(1f), color = if (shown) Navy else Cream.copy(alpha = .55f), fontWeight = FontWeight.Bold)
                    Text(if (shown) "${answer.points}" else "?", color = if (shown) Coral else Mint, fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("STRIKES", color = Cream.copy(alpha = .6f), fontSize = 11.sp)
            Spacer(Modifier.width(8.dp)); Text((1..3).joinToString(" ") { if (it <= state.strikes) "✕" else "○" }, color = Coral, fontSize = 24.sp)
            Spacer(Modifier.weight(1f)); state.buzzerTeam?.let { Text(if (it == 0) "☀ SUN BUZZED!" else "☾ MOON BUZZED!", color = Mint, fontWeight = FontWeight.Black) }
        }
        Spacer(Modifier.weight(1f))
        if (vm.role == Role.HOST) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallButton("+ STRIKE", Coral, Modifier.weight(1f), vm::strike)
                SmallButton("SWITCH", Purple, Modifier.weight(1f), vm::switchTeam)
                SmallButton("NEXT →", Mint, Modifier.weight(1f), vm::nextRound)
            }
            if (state.buzzerTeam != null) TextButton(vm::clearBuzz, Modifier.align(Alignment.CenterHorizontally)) { Text("Clear buzzer") }
        } else {
            Button(vm::buzz, Modifier.fillMaxWidth().height(68.dp), enabled = state.buzzerTeam == null,
                colors = ButtonDefaults.buttonColors(containerColor = if (vm.playerTeam == 0) Coral else Purple), shape = RoundedCornerShape(18.dp)) {
                Text(if (state.buzzerTeam == null) "BUZZ!" else "BUZZER LOCKED", fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
        }
        vm.error?.let { Text(it, Modifier.fillMaxWidth(), color = Coral, textAlign = TextAlign.Center, fontSize = 12.sp) }
    }
}

@Composable private fun BigButton(text: String, color: Color, onClick: () -> Unit) = Button(onClick, Modifier.fillMaxWidth().height(58.dp), colors = ButtonDefaults.buttonColors(containerColor = color), shape = RoundedCornerShape(16.dp)) { Text(text, fontWeight = FontWeight.Black, color = if (color == Mint) Navy else Color.White) }
@Composable private fun SmallButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) = Button(onClick, modifier.height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = color), contentPadding = PaddingValues(4.dp), shape = RoundedCornerShape(12.dp)) { Text(text, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (color == Mint) Navy else Color.White) }
@Composable private fun TeamChoice(text: String, color: Color, selected: Boolean, modifier: Modifier, click: () -> Unit) = Surface(modifier.clickable(onClick = click), color = if (selected) color else Color(0xFF252A50), shape = RoundedCornerShape(14.dp), border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = .5f))) { Text(text, Modifier.padding(16.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
@Composable private fun ScoreCard(name: String, score: Int, color: Color, active: Boolean, modifier: Modifier) = Surface(modifier, color = if (active) color else Color(0xFF252A50), shape = RoundedCornerShape(16.dp)) { Column(Modifier.padding(13.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold); Text("$score", fontSize = 32.sp, fontWeight = FontWeight.Black) } }
