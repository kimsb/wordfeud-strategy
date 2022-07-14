package mybot

import Bot
import applyStrategies
import domain.Game
import domain.Turn
import strategies.PenaltyStrategy
import strategies.WeightedStrategy

class MyBot(override val name: String, val strategies: List<WeightedStrategy>, val penaltyStrategy: PenaltyStrategy? = null): Bot {

    override fun makeTurn(game: Game): Turn {
        return applyStrategies(game, strategies, penaltyStrategy)
    }

    /*override fun makeTurn(game: Game): Turn {
        val allMovesSorted = game.board.findAllMovesSorted(game.rack)
        val highestScoringMove = allMovesSorted.firstOrNull()
            ?: return if (game.board.swapIsAllowed()) {
                bestSwap(game.rack)
            } else {
                Turn(turnType = PASS)
            }

        /*val winningEndgameTurn = winningEndgameMove(game, allMovesSorted)
        if (winningEndgameTurn != null) {
            return when(winningEndgameTurn.turnType) {
                MOVE -> Turn(turnType = MOVE, move = winningEndgameTurn.move)
                else -> Turn(turnType = PASS)
            }
        }*/

        //swap
        if (game.board.swapIsAllowed()) {
            //swap/pass if game ends and I win
            if (game.scorelessTurns == 2 && game.score >= game.opponentScore) {
                return if (game.rack.score() > 7) {
                    val tilesToSwap = game.rack.tiles.filter { Constants.letterScore(it) > 1 }
                    println("Avslutter kamp med bytte av $tilesToSwap")
                    Turn(turnType = SWAP, tilesToSwap = tilesToSwap)
                } else {
                    println("Avslutter kamp med PASS")
                    Turn(turnType = PASS)
                }
            }
            //swap when best move < 15p
            if (highestScoringMove.score < 15) {
                return bestSwap(game.rack)
            }
        }

        //board position

        // sjekk legg mot (bingo: ERANSTL), (openEAwayFromBingo: FARGBOK), (bigLetters: CREWPUD)
        // relativeScore

        //endgame
        if (game.board.bagCount() == 0) {
            val moveThatFinishesGame = allMovesSorted.firstOrNull { move1 ->
                moveFinishesGame(game, move1)
            }
            if (moveThatFinishesGame != null && moveWinsGame(game, moveThatFinishesGame)) {
                return Turn(turnType = MOVE, moveThatFinishesGame)
            }
        }


        //for performance - top 20 words
        val top10 = allMovesSorted.subList(0, minOf(allMovesSorted.size, 10))

        val sortedByDescendingRelativeScore = top10.sortedByDescending { it.score + getRelativeScore(game, it) }
        val highestRelativeScoringWord = sortedByDescendingRelativeScore.first()

        if (highestRelativeScoringWord.score != highestScoringMove.score) {
            println("ikke høyest scorende, legger ${highestRelativeScoringWord.word}: ${highestRelativeScoringWord.score}p, i stedet for ${highestScoringMove.word}: ${highestScoringMove.score}p")
            return Turn(turnType = MOVE, move = highestRelativeScoringWord)
        }

        //minst 15p ekstra for blank brikke
        //TODO Legger bestWithoutBlank: move: WAlKIE 53, bestWithout: KAIET 39 - her burde WAlKIE legges...
        if (containsBlank(highestScoringMove)) {
            val bestWithoutBlank = allMovesSorted.firstOrNull { move1 ->
                move1.addedTiles.map { it.first }.none { it.letter.isLowerCase() }
            }
            if (bestWithoutBlank != null && (bestWithoutBlank.score + 15) > highestScoringMove.score) {
                if (bestWithoutBlank.score < 15 && game.board.swapIsAllowed()) {
                    println("Har blank men bytter, for beste legg uten blank gir ${bestWithoutBlank.score} poeng")
                    return bestSwap(rack = game.rack)
                }
                println("Legger bestWithoutBlank: move: ${highestScoringMove.word} ${highestScoringMove.score}, bestWithout: ${bestWithoutBlank.word} ${bestWithoutBlank.score}")
                return Turn(turnType = MOVE, move = bestWithoutBlank)
            }
        }

        return Turn(turnType = MOVE, move = highestScoringMove)
    }

    private fun getRelativeScore(game: Game, move: Move): Double {
        var relativeScore = 0.0

        //for ENDGAME
        //ADD 20 relative points if one or more of opponents tiles cant ble placed
        /*if (stopsOpponentFromPlacingTile(game, move)) {
            relativeScore += 100
        }*/

        //leave
        //relativeScore -= leaveScore(rackAfterMove(game.rack, move))

        //board position
        //relativeScore -= opponentScoreAfterMove(game, move)

        return relativeScore
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

    private fun containsBlank(move: Move): Boolean {
        return move.addedTiles.map { it.first }.any { it.letter.isLowerCase() }
    }

    private fun moveFinishesGame(game: Game, move: Move): Boolean {
        return game.board.bagCount() == 0 && move.addedTiles.size == game.rack.tiles.size
    }

    private fun moveWinsGame(game: Game, move: Move): Boolean {
        val scoreOnOpponentsRack = game.board.lettersInBagOrOpponentsRack(game.rack).map {
            Constants.letterScore(it)
        }.sum()
        return game.board.bagCount() == 0
            && (game.score + move.score + scoreOnOpponentsRack > game.opponentScore - scoreOnOpponentsRack)
    }*/


}