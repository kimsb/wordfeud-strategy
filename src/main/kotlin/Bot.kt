import domain.Game
import domain.Turn

interface Bot {
    fun makeTurn(game: Game): Turn
}