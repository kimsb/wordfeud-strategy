import Constants.letterDistribution
import domain.*
import domain.TurnType.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import wordfeudapi.domain.ApiBoard
import kotlin.system.measureTimeMillis

class Simulator(
    val myBot: Bot,
    val controlBot: Bot
) {

    fun simulate(rounds: Int) {

        val simulationResults: List<SimulatedRound>
        val time = measureTimeMillis {
            val deferredSimulationResults = (1..rounds).map { i ->
                GlobalScope.async {
                    println("Starting simulation of round #$i")
                    simulateRound()
                }
            }
            runBlocking {
                simulationResults = deferredSimulationResults.awaitAll()
            }
        }

        for (i in 1..simulationResults.size) {
            val simulationResult = simulationResults[i - 1]
            println("Game ${i}a: ${simulationResult.simulatedGameA.myBotScore} - ${simulationResult.simulatedGameA.controlBotScore}")
            println("MyBot: ${simulationResult.simulatedGameA.myBotTurns.print()}")
            println("ControlBot: ${simulationResult.simulatedGameA.controlBotTurns.print()}\n")
            println("Game ${i}b: ${simulationResult.simulatedGameB.myBotScore} - ${simulationResult.simulatedGameB.controlBotScore}")
            println("MyBot: ${simulationResult.simulatedGameB.myBotTurns.print()}")
            println("ControlBot: ${simulationResult.simulatedGameB.controlBotTurns.print()}\n")
        }
        println("Simulation finished in $time ms\n")
        val myBotWins = simulationResults.map {
            when {
                it.simulatedGameA.myBotScore > it.simulatedGameA.controlBotScore -> 1.0
                it.simulatedGameA.myBotScore == it.simulatedGameA.controlBotScore -> 0.5
                else -> 0.0
            } + when {
                it.simulatedGameB.myBotScore > it.simulatedGameB.controlBotScore -> 1.0
                it.simulatedGameB.myBotScore == it.simulatedGameB.controlBotScore -> 0.5
                else -> 0.0
            }
        }.sum()

        println("MyBot: $myBotWins wins (${myBotWins / (rounds * 2) * 100}%) total score: ${
                simulationResults.map { it.simulatedGameA.myBotScore + it.simulatedGameB.myBotScore }.sum()}")
        println("MyBot: ${(rounds * 2) - myBotWins} wins (${100 - (myBotWins / (rounds * 2) * 100)}%) total score: ${
            simulationResults.map { it.simulatedGameA.controlBotScore + it.simulatedGameB.controlBotScore }.sum()}")

    }

    private fun List<Turn>.print(): String {
        return this.joinToString(",") {
            when (it.turnType) {
                MOVE -> it.move!!.word
                SWAP -> "<swap [${it.tilesToSwap}]>"
                PASS -> "<pass>"
            }
        }
    }

    private fun simulateRound(): SimulatedRound {
        var letters = letterDistribution.toList()
        letters = letters.shuffled()

        return SimulatedRound(
            simulatedGameA = simulateGame(bag = Bag(letters), myBotStarts = true),
            simulatedGameB = simulateGame(bag = Bag(letters), myBotStarts = false)
        )
    }

    private fun simulateGame(bag: Bag, myBotStarts: Boolean): SimulatedGame {
        var player1 = Player(bot = if (myBotStarts) myBot else controlBot, rack = Rack(bag.pickTiles(7)))
        var player2 = Player(bot = if (myBotStarts) controlBot else myBot, rack = Rack(bag.pickTiles(7)))
        var board = emptyBoard()
        var scorelessTurns = 0
        var player1sTurn = true
        var gameIsRunning = true

        while (gameIsRunning) {

            val playerInMove = if (player1sTurn) player1 else player2
            val opponent = if (player1sTurn) player2 else player1
            val game = Game(
                board = board,
                rack = playerInMove.rack,
                score = playerInMove.score,
                opponentScore = opponent.score,
                scorelessTurns = scorelessTurns
            )
            val turn = playerInMove.bot.makeTurn(game)
            playerInMove.turns.add(turn)

            when (turn.turnType) {
                MOVE -> {
                    scorelessTurns = 0
                    board = game.board.withMove(turn.move!!)
                    playerInMove.score = playerInMove.score + turn.move.score
                    playerInMove.rack = playerInMove.rack.swap(
                        toSwap = turn.move.addedTiles.map { it.first.letter },
                        newLetters = bag.pickTiles(turn.move.addedTiles.size)
                    )
                }
                SWAP -> {
                    scorelessTurns++
                    playerInMove.rack =
                        playerInMove.rack.swap(
                            toSwap = turn.tilesToSwap,
                            newLetters = bag.swapTiles(turn.tilesToSwap)
                        )
                }
                PASS -> {
                    scorelessTurns++
                }
            }

            if (scorelessTurns == 3) {
                playerInMove.score = playerInMove.score - playerInMove.rack.score()
                opponent.score = opponent.score - opponent.rack.score()
                gameIsRunning = false
            } else if (playerInMove.rack.tiles.isEmpty()) {
                playerInMove.score = playerInMove.score + opponent.rack.score()
                gameIsRunning = false
            }

            player1 = if (player1sTurn) playerInMove else opponent
            player2 = if (player1sTurn) opponent else playerInMove
            player1sTurn = !player1sTurn
        }

        val myPlayer = if (myBotStarts) player1 else player2
        val opponent = if (myBotStarts) player2 else player1
        return Simulator.SimulatedGame(
            myBotScore = myPlayer.score,
            controlBotScore = opponent.score,
            myBotTurns = myPlayer.turns,
            controlBotTurns = opponent.turns
        )
    }

    data class Bag(
        var tiles: List<Char>
    ) {
        fun pickTiles(count: Int): List<Char> {
            val removed = tiles.subList(0, minOf(tiles.size, count))
            tiles = tiles.drop(removed.size)
            return removed
        }

        fun swapTiles(toSwap: List<Char>): List<Char> {
            check(tiles.size >= 7) { "Trying to swap when bag only contains ${tiles.size} letters" }
            val removed = tiles.subList(0, toSwap.size)
            tiles = tiles.drop(toSwap.size)
            tiles = tiles + toSwap
            tiles = tiles.shuffled()
            return removed
        }
    }

    data class Player(
        val bot: Bot,
        var rack: Rack,
        var turns: MutableList<Turn> = mutableListOf(),
        var score: Int = 0,
    )

    data class SimulatedRound(
        val simulatedGameA: SimulatedGame,
        val simulatedGameB: SimulatedGame
    )

    data class SimulatedGame(
        val myBotTurns: List<Turn>,
        val controlBotTurns: List<Turn>,
        val myBotScore: Int,
        val controlBotScore: Int
    )

    private fun emptyBoard(): Board {
        val standardApiBoard = ApiBoard(
            arrayOf(
                intArrayOf(2, 0, 0, 0, 4, 0, 0, 1, 0, 0, 4, 0, 0, 0, 2),
                intArrayOf(0, 1, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 0),
                intArrayOf(0, 0, 3, 0, 0, 0, 1, 0, 1, 0, 0, 0, 3, 0, 0),
                intArrayOf(0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 2, 0, 0, 0),
                intArrayOf(4, 0, 0, 0, 3, 0, 1, 0, 1, 0, 3, 0, 0, 0, 4),
                intArrayOf(0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0),
                intArrayOf(0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0),
                intArrayOf(1, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 1),
                intArrayOf(0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0),
                intArrayOf(0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0),
                intArrayOf(4, 0, 0, 0, 3, 0, 1, 0, 1, 0, 3, 0, 0, 0, 4),
                intArrayOf(0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 2, 0, 0, 0),
                intArrayOf(0, 0, 3, 0, 0, 0, 1, 0, 1, 0, 0, 0, 3, 0, 0),
                intArrayOf(0, 1, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 0),
                intArrayOf(2, 0, 0, 0, 4, 0, 0, 1, 0, 0, 4, 0, 0, 0, 2)
            )
        )
        return Board(standardApiBoard, emptyArray())
    }
}