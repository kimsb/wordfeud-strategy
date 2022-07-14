import domain.Game
import domain.Turn
import domain.TurnType
import strategies.PenaltyStrategy
import strategies.WeightedStrategy

fun applyStrategies(game: Game, strategies: List<WeightedStrategy>, penaltyStrategy: PenaltyStrategy? = null): Turn {
    val allMoves = game.board.findAllMovesSorted(game.rack)

    val weightedScores = strategies.associate { weightedStrategy ->
        weightedStrategy.name to weightedStrategy.relativeScore(game = game, allMovesSorted = allMoves)
    }

    val moveToWeightedScores = allMoves.mapIndexed { index, move ->
        move to weightedScores.values.sumByDouble { it[index] }
    }

    val penalties = penaltyStrategy?.penalties(game, allMoves) ?: emptyList()
    //add penalties
    val withPenalties = moveToWeightedScores.mapIndexed { index, moveToWeightedScore ->
        moveToWeightedScore.first to (moveToWeightedScore.second - penalties.getOrElse(index) { 0.0 })
    }

    val sortedByWeightScoreDescending = withPenalties.sortedByDescending { it.second }

    val move = sortedByWeightScoreDescending.firstOrNull()?.first

    if (move != null && (move.score != allMoves.first().score || move.word != allMoves.first().word)) {
        println("legger heller ${move.word}: ${move.score}p enn ${allMoves.first().word}: ${allMoves.first().score}")
    }

    return when {
        move != null -> Turn(turnType = TurnType.MOVE, move = move)
        game.board.swapIsAllowed() -> Turn(turnType = TurnType.SWAP, tilesToSwap = game.rack.tiles)
        else -> Turn(turnType = TurnType.PASS)
    }
}