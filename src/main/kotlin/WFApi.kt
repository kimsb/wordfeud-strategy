import domain.Board
import domain.Rack
import domain.TurnType.*
import wordfeudapi.RestWordFeudClient
import wordfeudapi.domain.Game
import java.lang.Thread.sleep

class WFApi(
    private val bot: Bot
) {
    private val wfClient = RestWordFeudClient()

    init {
        wfClient.logon(bot.name, bot.name)
        println("Logged in as ${bot.name}")
        botLoop()
    }

    private fun botLoop() {
        while (true) {
            acceptInvites()

            val gameIdsMyTurn = wfClient.games
                .filter(Game::isRunning)
                .filter(Game::isMyTurn)
                .map(Game::getId)

            gameIdsMyTurn.forEach {
                val game = wfClient.getGame(it)
                makeMove(game)
            }

            if (gameIdsMyTurn.isEmpty()) {
                sleep(1000)
            }
        }
    }

    private fun acceptInvites() {
        wfClient.status.invitesReceived.forEach {
            //Only accept norwegian bokmål
            if (it.ruleset.apiIntRepresentation == 1) {
                println("Starting game against ${it.inviter}")
                wfClient.acceptInvite(it.id)
            } else {
                wfClient.rejectInvite(it.id)
            }
        }
    }

    private fun makeMove(game: Game) {
        val turn = bot.makeTurn(
            domain.Game(
                Board(wfClient.getBoard(game), game.tiles),
                Rack(game.myRack.chars().toList()),
                score = game.me.score,
                opponentScore = game.opponent.score
            )
        )
        print("Against ${game.opponentName}: ")
        when (turn.turnType) {
            MOVE -> {
                val tileMove = turn.move!!.toTileMove()
                println("Playing ${tileMove.word} for ${tileMove.points} points")
                wfClient.makeMove(game, tileMove)
            }
            SWAP -> { //TODO finne ut hvordan swappe blank
                val toSwap = turn.tilesToSwap.filter { it != '*' }
                println("Swapping [${toSwap.joinToString("")}]")
                wfClient.swap(game, toSwap.toCharArray())
            }
            PASS -> {
                println("Passing")
                wfClient.pass(game)
            }
        }
    }
}
