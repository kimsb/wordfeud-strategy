package mybot

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LeaveTest {

    @Test
    fun testLeaveScore() {
        val leaveScore = leaveScore("ABC")

        assertEquals(10, leaveScore)
    }
}

