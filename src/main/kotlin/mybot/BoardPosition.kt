package mybot

import domain.Game
import domain.Move
import domain.Rack

//TODO dette tar for lang tid + vektes slik at man legger for sjeldent maks
fun opponentScoreAfterMove(game: Game, move: Move): Int {
    val eranstl = Rack("ERANSTL".toList())
    val fargbok = Rack("FARGBOK".toList())
    val crewpud = Rack("CREWPUD".toList())

    val boardWithMove = game.board.withMove(move)

    val allMovesEranstl = boardWithMove.findAllMovesSorted(eranstl)
    val allMovesFargbok = boardWithMove.findAllMovesSorted(fargbok)
    val allMovesCrewpud = boardWithMove.findAllMovesSorted(crewpud)

    return ((allMovesEranstl.firstOrNull()?.score ?: 0)
    + (allMovesFargbok.firstOrNull()?.score ?: 0)
    + (allMovesCrewpud.firstOrNull()?.score ?: 0)) / 3
}