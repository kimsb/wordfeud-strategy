package mybot

import Bot
import domain.Game
import domain.Move
import domain.Rack
import domain.Turn
import domain.TurnType.*

class MyBot(override val name: String) : Bot {

    override fun makeTurn(game: Game): Turn {
        val allMovesSorted = game.board.findAllMovesSorted(game.rack)
        val move = allMovesSorted.firstOrNull()
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
            if (move.score < 15) {
                return bestSwap(game.rack)
            }
        }

        //board position

        // sjekk legg mot (bingo: ERANSTL), (openEAwayFromBingo: FARGBOK), (bigLetters: CREWPUD)
        // relativeScore

        //leave
        //kvitte seg med W

        //endgame
        if (game.board.bagCount() == 0) {
            val moveThatFinishesGame = allMovesSorted.firstOrNull { move1 ->
                moveFinishesGame(game, move1)
            }
            if (moveThatFinishesGame != null && moveWinsGame(game, moveThatFinishesGame)) {
                return Turn(turnType = MOVE, moveThatFinishesGame)
            }
        }

        //minst 15p ekstra for blank brikke
        //Legger bestWithoutBlank: move: WAlKIE 53, bestWithout: KAIET 39 - her burde WAlKIE legges...
        if (containsBlank(move)) {
            val bestWithoutBlank = allMovesSorted.firstOrNull { move1 ->
                move1.addedTiles.map { it.first }.none { it.letter.isLowerCase() }
            }
            if (bestWithoutBlank != null && (bestWithoutBlank.score + 15) > move.score) {
                if (bestWithoutBlank.score < 15 && game.board.swapIsAllowed()) {
                    println("Har blank men bytter, for beste legg uten blank gir ${bestWithoutBlank.score} poeng")
                    return bestSwap(rack = game.rack)
                }
                println("Legger bestWithoutBlank: move: ${move.word} ${move.score}, bestWithout: ${bestWithoutBlank.word} ${bestWithoutBlank.score}")
                return Turn(turnType = MOVE, move = bestWithoutBlank)
            }
        }


        return Turn(turnType = MOVE, move = move)
    }

    private fun containsBlank(move: Move): Boolean {
        return move.addedTiles.map { it.first }.any { it.letter.isLowerCase() }
    }

    private fun bestSwap(rack: Rack): Turn {
        val rackWithoutBlank = if (rack.contains('*')) rack.without('*') else rack

        val tilesToSwap = when {
            rackWithoutBlank.tiles.contains('E') && rackWithoutBlank.tiles.contains('R') ->
                rackWithoutBlank.without('E').without('R').tiles
            rackWithoutBlank.tiles.contains('E') && rackWithoutBlank.tiles.contains('N') ->
                rackWithoutBlank.without('E').without('N').tiles
            rackWithoutBlank.tiles.contains('E') && rackWithoutBlank.tiles.contains('S') ->
                rackWithoutBlank.without('E').without('S').tiles
            rackWithoutBlank.tiles.contains('E') && rackWithoutBlank.tiles.contains('T') ->
                rackWithoutBlank.without('E').without('T').tiles
            rackWithoutBlank.tiles.contains('A') && rackWithoutBlank.tiles.contains('R') ->
                rackWithoutBlank.without('A').without('R').tiles
            rackWithoutBlank.tiles.contains('A') && rackWithoutBlank.tiles.contains('N') ->
                rackWithoutBlank.without('A').without('N').tiles
            rackWithoutBlank.tiles.contains('A') && rackWithoutBlank.tiles.contains('S') ->
                rackWithoutBlank.without('A').without('S').tiles
            rackWithoutBlank.tiles.contains('A') && rackWithoutBlank.tiles.contains('T') ->
                rackWithoutBlank.without('A').without('T').tiles
            rackWithoutBlank.tiles.contains('E') ->
                rackWithoutBlank.without('E').tiles
            else -> rackWithoutBlank.tiles
        }
        return Turn(turnType = SWAP, tilesToSwap = tilesToSwap)
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
    }


}

data class RelativeMove(
    val move: Move,
    val relativeScore: Double
) {
    fun add(score: Double): RelativeMove {
        return this.copy(relativeScore = relativeScore + score)
    }

    fun subtract(score: Double): RelativeMove {
        return this.copy(relativeScore = relativeScore - score)
    }
}