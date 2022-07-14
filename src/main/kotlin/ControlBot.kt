import domain.Game
import domain.Turn
import strategies.WeightedStrategy

class ControlBot(val strategies: List<WeightedStrategy>) : Bot {

    override val name = "ControlBot"

    override fun makeTurn(game: Game): Turn {
        return applyStrategies(game, strategies)
    }
}