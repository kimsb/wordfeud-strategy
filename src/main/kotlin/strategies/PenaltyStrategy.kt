package strategies

import domain.Game
import domain.Move

interface PenaltyStrategy {
    val name: StrategyName
    val penalty: Double

    fun penalties(game: Game, allMovesSorted: List<Move>): List<Double>
}