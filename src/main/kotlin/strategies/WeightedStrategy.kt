package strategies

import domain.Game
import domain.Move

interface WeightedStrategy {
    val weight: Double
    val name: StrategyName

    fun relativeScore(game: Game, allMovesSorted: List<Move>): List<Double>
}