package strategies

import domain.Game
import domain.Move
import domain.Rack
import strategies.StrategyName.MAXIMIZE_VOWEL_RATIO_LEAVE

class MaximizeVowelRatioLeave(override val weight: Double) : WeightedStrategy {

    override val name = MAXIMIZE_VOWEL_RATIO_LEAVE

    override fun relativeScore(game: Game, allMovesSorted: List<Move>): List<Double> {
        if (allMovesSorted.isEmpty()) {
            return emptyList()
        }

        val vowelRatioScores = allMovesSorted.map { move ->
            vowelRatioScore(rackAfterMove(game.rack, move).tiles)
        }
        val min = vowelRatioScores.minOrNull()!!
        val max = vowelRatioScores.maxOrNull()!!

        return vowelRatioScores.map {
            weight * relativeScore(internalScore = it, min = min, max = max)
        }
    }

    private fun relativeScore(internalScore: Double, min: Double, max: Double): Double {
        return when (internalScore) {
            max -> 1.0
            min -> 0.0
            else -> internalScore / (min + max)
        }
    }

    private fun rackAfterMove(rack: Rack, move: Move): Rack {
        var rackAfterMove = rack
        move.addedTiles.map { it.first.letter }.forEach {
            rackAfterMove = if (it.isLowerCase()) {
                rackAfterMove.without('*')
            } else {
                rackAfterMove.without(it)
            }
        }
        return rackAfterMove
    }

    private fun vowelRatioScore(leave: List<Char>): Double {
        vowelCount(leave) / leave.size.toDouble()

        val vowelCount = vowelCount(leave)

        //TODO denne burde skrives smartere / mer rettferdig
        return when (leave.size) {
            1 -> 0.5
            2 -> when (vowelCount) {
                1 -> 0.85
                else -> 0.7
            }
            3 -> when (vowelCount) {
                1 -> 1.0
                2 -> 0.67
                else -> 0.45
            }
            4 -> when (vowelCount) {
                2 -> 0.85
                1 -> 0.75
                3 -> 0.3
                else -> 0.1
            }
            5 -> when (vowelCount) {
                2 -> 0.9
                1 -> 0.6
                3 -> 0.4
                4 -> 0.15
                else -> 0.05
            }
            6 -> when (vowelCount) {
                2 -> 1.0
                3 -> 0.75
                1 -> 0.3
                4 -> 0.2
                else -> 0.0
            }
            else -> 0.0
        }
    }

    private fun vowelCount(leave: List<Char>): Int {
        return leave.filter { isVowel(it) }.size
    }

    private fun isVowel(char: Char): Boolean {
        return when (char) {
            'A' -> true
            'E' -> true
            'I' -> true
            'O' -> true
            'U' -> true
            'Y' -> true
            'Æ' -> true
            'Ø' -> true
            'Å' -> true
            else -> false
        }
    }
}