package mybot

import domain.*
import domain.TurnType.MOVE

//TODO return pass if it wins the game
fun winningEndgameMove(game: Game, allMoves: List<Move>): Turn? {
    if (game.board.bagCount() > 0) {
        return null
    }
    //println("Entering endgame")
    //println("checking ${allMoves.size} moves...")

    allMoves.forEach { move ->

        //if opponent cant move && you win - pass out
        if (game.board.findAllMovesSorted(Rack(game.board.lettersInBagOrOpponentsRack(game.rack).toList())).isEmpty()) {
            return Turn(turnType = TurnType.PASS)
        }

        if (winsGame(
                board = game.board,
                rack = game.rack,
                score = game.score,
                opponentScore = game.opponentScore,
                move = move,
                moveCount = 1
            )
        ) {
            //println("Winning game with: ${move.word}!")
            return Turn(turnType = MOVE, move = move)
        }
    }
    //println("Can't find a winning move... :(")
    return null
}

private fun winsGame(
    board: Board,
    rack: Rack,
    score: Int,
    opponentScore: Int,
    move: Move,
    moveCount: Int
): Boolean {
    val newBoard = board.withMove(move)
    val newScore = score + move.score
    val newRack = move.addedTiles.map { it.first.letter }.fold(rack) { rack1, l -> rack1.without(l) }
    val opponentRack = Rack(newBoard.lettersInBagOrOpponentsRack(newRack).toList())

    if (newRack.tiles.isEmpty()) {
        return if (newScore + opponentRack.score() > opponentScore - opponentRack.score()) {
            //println("winning this game!")
            true
        } else {
            false
        }
    }
    val allOpponentMoves = newBoard.findAllMovesSorted(rack = opponentRack)
    //println("${"\t".repeat(moveCount)}${move.word}: checking ${allOpponentMoves.size} opponentMoves...")
    if (allOpponentMoves.isEmpty()) {

        if(newScore - newRack.score() > opponentScore - opponentRack.score()) {
            return true
        }

        val allMoves = newBoard.findAllMovesSorted(rack = newRack)
        //println("Opponent can't move - checking ${allMoves.size} moves...")
        return allMoves.any {
            winsGame(
                board = newBoard,
                rack = newRack,
                score = newScore,
                opponentScore = opponentScore,
                move = it,
                moveCount = moveCount + 1
            )
        }
    }
    return allOpponentMoves.all {
        opponentLosesGame(
            board = newBoard,
            opponentRack = opponentRack,
            score = newScore,
            opponentScore = opponentScore,
            opponentMove = it,
            moveCount = moveCount + 1
        )
    }
}

private fun opponentLosesGame(
    board: Board,
    opponentRack: Rack,
    score: Int,
    opponentScore: Int,
    opponentMove: Move,
    moveCount: Int
): Boolean {
    val newBoard = board.withMove(opponentMove)
    val newOpponentScore = opponentScore + opponentMove.score
    val newOpponentRack =
        opponentMove.addedTiles.map { it.first.letter }.fold(opponentRack) { rack1, l -> rack1.without(l) }
    val rack = Rack(newBoard.lettersInBagOrOpponentsRack(newOpponentRack).toList())

    if (newOpponentRack.tiles.isEmpty()) {
        return if (opponentScore + rack.score() > score - rack.score()) {
            //println("opponent wins game")
            false
        } else {
            true
        }
    }
    val allMoves = newBoard.findAllMovesSorted(rack = rack)
    //println("${"\t".repeat(moveCount)}${opponentMove.word}: checking ${allMoves.size} moves...")
    return allMoves.any {
        winsGame(
            board = newBoard,
            rack = rack,
            score = score,
            opponentScore = newOpponentScore,
            move = it,
            moveCount = moveCount + 1
        )
    }
}