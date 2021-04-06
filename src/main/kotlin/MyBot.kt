import domain.Game
import domain.Turn
import domain.TurnType.*

class MyBot: Bot {

    override fun makeTurn(game: Game): Turn {
        val move = game.board.findAllMovesSorted(game.rack).firstOrNull()

        return if (move != null) {
            if (move.score < 15 && game.board.swapIsAllowed()) {
                if (game.rack.contains('E')) {
                    Turn(turnType = SWAP, tilesToSwap = game.rack.without('E').tiles)
                } else {
                    Turn(turnType = SWAP, tilesToSwap = game.rack.tiles)
                }
            } else {
                Turn(turnType = MOVE, move = move)
            }
        } else if (game.board.swapIsAllowed()) {
            Turn(SWAP, tilesToSwap = game.rack.tiles)
        } else {
            Turn(PASS)
        }
    }
}