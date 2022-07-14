package strategies

import domain.Game
import domain.Move
import strategies.StrategyName.DONT_USE_BLANK

class DontUseBlank(override val penalty: Double): PenaltyStrategy {

    override val name = DONT_USE_BLANK

    override fun penalties(game: Game, allMovesSorted: List<Move>): List<Double> {

        return allMovesSorted.map { move ->
            val usesBlank = move.addedTiles.any { tile -> tile.first.letter.isLowerCase() }
            if (usesBlank) {
                penalty
            } else
                0.0
        }
    }

}

