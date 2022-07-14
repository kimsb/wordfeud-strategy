package strategies

import domain.Game
import domain.Move
import strategies.StrategyName.MAXIMIZE_SCORE

class MaximizeScore(override val weight: Double): WeightedStrategy {

    override val name = MAXIMIZE_SCORE

    override fun relativeScore(game: Game, allMovesSorted: List<Move>): List<Double> {

        //TODO bør turn inn her? pass og swap?

        if (allMovesSorted.isEmpty()) {
            return emptyList()
        }

        val maxScore = allMovesSorted.first().score
        val minScore = allMovesSorted.last().score

        return allMovesSorted.map { move ->
            weight * relativeScore(internalScore = move.score, min = minScore, max = maxScore)
        }
    }

    private fun relativeScore(internalScore: Int, min: Int, max: Int): Double {
        return when (internalScore) {
            max -> 1.0
            min -> 0.0
            else -> internalScore / (min + max).toDouble()
        }
    }
}