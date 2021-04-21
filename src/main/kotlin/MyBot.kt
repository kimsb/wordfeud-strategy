import domain.Game
import domain.Rack
import domain.Turn
import domain.TurnType.*

class MyBot(override val name: String) : Bot {

    override fun makeTurn(game: Game): Turn {
        val move = game.board.findAllMovesSorted(game.rack).firstOrNull()
            ?: return if (game.board.swapIsAllowed()) {
                bestSwap(game.rack)
            } else {
                Turn(turnType = PASS)
            }

        //swap
        if (game.board.swapIsAllowed()) {
            //swap when best move < 15p
            if (move.score < 15) {
                return bestSwap(game.rack)
            }
        }

        //board position

        //leave

        //minst 15p ekstra for blank brikke

        //endgame

        return Turn(turnType = MOVE, move = move)
    }

    private fun bestSwap(rack: Rack): Turn {
        val tilesToSwap = when {
            rack.tiles.contains('E') && rack.tiles.contains('R') -> rack.without('E').without('R').tiles
            rack.tiles.contains('E') && rack.tiles.contains('N') -> rack.without('E').without('N').tiles
            rack.tiles.contains('E') && rack.tiles.contains('S') -> rack.without('E').without('S').tiles
            rack.tiles.contains('E') && rack.tiles.contains('T') -> rack.without('E').without('T').tiles
            rack.tiles.contains('A') && rack.tiles.contains('R') -> rack.without('A').without('R').tiles
            rack.tiles.contains('A') && rack.tiles.contains('N') -> rack.without('A').without('N').tiles
            rack.tiles.contains('A') && rack.tiles.contains('S') -> rack.without('A').without('S').tiles
            rack.tiles.contains('A') && rack.tiles.contains('T') -> rack.without('A').without('T').tiles
            rack.tiles.contains('E') -> rack.without('E').tiles
            else -> rack.tiles
        }
        return Turn(turnType = SWAP, tilesToSwap = tilesToSwap)
    }
}