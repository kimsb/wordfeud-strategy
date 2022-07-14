package mybot

import domain.Rack
import domain.Turn
import domain.TurnType

/*private fun bestSwap(rack: Rack): Turn {
    val rackWithoutBlank = if (rack.contains('*')) rack.without('*') else rack


} */

fun bestSwap(rack: Rack): Turn {
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
        return Turn(turnType = TurnType.SWAP, tilesToSwap = tilesToSwap)
    }