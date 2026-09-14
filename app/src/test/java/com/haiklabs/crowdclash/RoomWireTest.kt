package com.haiklabs.crowdclash

import org.junit.Assert.assertEquals
import org.junit.Test

class RoomWireTest {
    @Test fun stateRoundTripsThroughWireFormat() {
        val original = GameState("ABC234", 3, setOf(0, 2), 42, 18, 1, 2, 0, 99)
        assertEquals(original, RoomWire.decode(RoomWire.encode(original)))
    }

    @Test fun everyQuestionTotalsOneHundredPoints() {
        Questions.all.forEach { assertEquals(100, it.answers.sumOf(SurveyAnswer::points)) }
    }
}
