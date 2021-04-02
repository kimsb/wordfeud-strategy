import domain.Board
import domain.Move
import domain.Rack
import domain.Tile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import wordfeudapi.domain.ApiBoard

class SimulatedGame {

    private val emptyBoard: Board
    private val letters =
        "AAAAAAABBBCDDDDDEEEEEEEEEFFFFGGGGHHHIIIIIIJJKKKKLLLLLMMMNNNNNNOOOOPPRRRRRRRSSSSSSSTTTTTTTUUUVVVWYÆØØÅÅ**"

    init {
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
        emptyBoard = Board(standardApiBoard, emptyArray())
        simulate(200)
    }

    private fun pickTiles(bag: List<Char>, count: Int): List<Char> {
        return if (bag.size < count) {
            bag
        } else {
            bag.subList(0, count)
        }
    }

    data class SimulationResult(
        val score1: Int,
        val score2: Int,
        val words1: List<String>,
        val words2: List<String>
    )

    data class DoubleGameSimulationResult(
        val bag: List<Char>,
        val gameNumber: Int,
        val score1a: Int,
        val score2a: Int,
        val score1b: Int,
        val score2b: Int,
        val words1a: List<String>,
        val words2a: List<String>,
        val words1b: List<String>,
        val words2b: List<String>
    )

    fun simulateDoubleGame(gameNumber: Int): DoubleGameSimulationResult {
        var bag = letters.toList()
        bag = bag.shuffled()
        val simulationA = simulateGame(bag, true)
        val simulationB = simulateGame(bag, false)
        return DoubleGameSimulationResult(
            bag = bag,
            gameNumber = gameNumber,
            score1a = simulationA.score1,
            score2a = simulationA.score2,
            score1b = simulationB.score1,
            score2b = simulationB.score2,
            words1a = simulationA.words1,
            words2a = simulationA.words2,
            words1b = simulationB.words1,
            words2b = simulationB.words2
        )
    }

    fun simulateGame(initialBag: List<Char>, player1Starts: Boolean): SimulationResult {
        var board = emptyBoard
        var bag = initialBag
        var rack1: Rack
        var rack2: Rack
        if (player1Starts) {
            rack1 = Rack(pickTiles(bag, 7))
            bag = bag.drop(7)
            rack2 = Rack(pickTiles(bag, 7))
            bag = bag.drop(7)
        } else {
            rack2 = Rack(pickTiles(bag, 7))
            bag = bag.drop(7)
            rack1 = Rack(pickTiles(bag, 7))
            bag = bag.drop(7)
        }

        var score1 = 0
        var score2 = 0
        var player1sTurn = player1Starts
        var gameIsFinished = false

        val moves1 = mutableListOf<String>()
        val moves2 = mutableListOf<String>()

        var passes = 0

        while (!gameIsFinished) {
            if (passes == 3) {
                //println("game passed out")
                break
            }
            if (player1sTurn) {
                val move = getHighestScoringMoveWithoutEIfSlightlyLessPoints(board, rack1)
                player1sTurn = false

                //pass hvis ingen legg
                if (move == null) {
                    passes += 1
                    if (bag.size > 7) {
                        println("kan ikke legge, bytter ${rack1.tiles}")
                        val toSwap = rack1.tiles
                        rack1 = Rack(pickTiles(bag, 7))
                        bag = bag.drop(7)
                        bag = bag + toSwap
                        bag = bag.shuffled()
                    }
                } // bytter hvis score er mindre enn 20 poeng
                else if (move.score < 20 && bag.size >= 7 && passes < 2) {
                    passes += 1
                    println("bytter ${rack1.tiles}")
                    val toSwap = rack1.tiles
                    if (rack1.contains('E')) {
                        rack1 = Rack(pickTiles(bag, 6) + 'E')
                    } else {
                        rack1 = Rack(pickTiles(bag, 7))
                    }
                    bag = bag.drop(7)
                    bag = bag + toSwap
                    bag = bag.shuffled()
                }
                /*if (move == null) {
                    passes += 1
                } */else {
                    passes = 0
                    // legg på brett
                    board = board.withMove(move)
                    // tell score
                    score1 += move.score
                    // fjern brikker fra rack
                    move.addedTiles.forEach {
                        rack1 = rack1.without(if (it.first.letter.isLowerCase()) '*' else it.first.letter)
                    }
                    // trekk nye brikker
                    rack1 = Rack(rack1.tiles + pickTiles(bag, 7 - rack1.tiles.size))
                    bag = bag.drop(minOf(bag.size, move.addedTiles.size))
                    gameIsFinished = rack1.tiles.isEmpty()

                    moves1.add(move.word)
                }
            } else {
                player1sTurn = true

                val move = getHighestScoringMove(board, rack2)
                if (move == null) {
                    passes += 1
                } else {
                    passes = 0
                    // legg på brett
                    board = board.withMove(move)
                    // tell score
                    score2 += move.score
                    // fjern brikker fra rack
                    move.addedTiles.forEach {
                        rack2 = rack2.without(if (it.first.letter.isLowerCase()) '*' else it.first.letter)
                    }
                    // trekk nye brikker
                    rack2 = Rack(rack2.tiles + pickTiles(bag, 7 - rack2.tiles.size))
                    bag = bag.drop(minOf(bag.size, move.addedTiles.size))
                    gameIsFinished = rack2.tiles.isEmpty()

                    moves2.add(move.word)
                }
            }
        }
        return SimulationResult(
            score1,
            score2,
            moves1,
            moves2
        )
    }

    fun simulate(numGames: Int) {
        val start = System.currentTimeMillis()

        val deferredSimulationResults = (1..numGames).map { i ->
            GlobalScope.async {
                println("Simulating game #$i")
                simulateDoubleGame(i)
            }
        }

        runBlocking {

            val simulationResults = deferredSimulationResults.awaitAll()
            val ms = System.currentTimeMillis() - start

            val allBags = simulationResults.map { it.bag }
            val allScores1a = simulationResults.map { it.score1a }
            val allScores2a = simulationResults.map { it.score2a }
            val allMoves1a = simulationResults.map { it.words1a }
            val allMoves2a = simulationResults.map { it.words2a }
            val allScores1b = simulationResults.map { it.score1b }
            val allScores2b = simulationResults.map { it.score2b }
            val allMoves1b = simulationResults.map { it.words1b }
            val allMoves2b = simulationResults.map { it.words2b }

            var wins1 = 0.0
            var wins2 = 0.0
            for (i in 0 until numGames) {
                println("Game #${i + 1}a: ${allScores1a[i]} - ${allScores2a[i]}, bag: ${allBags[i].joinToString()}")
                println("Player 1: ${allMoves1a[i].joinToString(",")}")
                println("Player 2: ${allMoves2a[i].joinToString(",")}\n")
                when {
                    allScores1a[i] > allScores2a[i] -> {
                        wins1 += 1
                    }
                    allScores1a[i] == allScores2a[i] -> {
                        wins1 += 0.5
                        wins2 += 0.5
                    }
                    else -> {
                        wins2 += 1
                    }
                }
                println("Game #${i + 1}b: ${allScores1b[i]} - ${allScores2b[i]}")
                println("Player 1: ${allMoves1b[i].joinToString(",")}")
                println("Player 2: ${allMoves2b[i].joinToString(",")}\n")
                when {
                    allScores1b[i] > allScores2b[i] -> {
                        wins1 += 1
                    }
                    allScores1b[i] == allScores2b[i] -> {
                        wins1 += 0.5
                        wins2 += 0.5
                    }
                    else -> {
                        wins2 += 1
                    }
                }
            }
            println("Simulation completed in $ms ms\n")
            println("Player 1: $wins1 wins (${wins1 / (numGames*2) * 100}%) total score: ${allScores1a.sum() + allScores1b.sum()}")
            println("Player 2: $wins2 wins (${wins2 / (numGames*2) * 100}%) total score: ${allScores2a.sum() + allScores2b.sum()}")
        }
    }

    fun getHighestScoringMove(board: Board, rack: Rack): Move? {
        return board.findAllMoves(rack).maxByOrNull(Move::score)
    }

    fun getHighestScoringMoveWithoutEIfSlightlyLessPoints(board: Board, rack: Rack): Move? {
        val sortedByDescending = board.findAllMoves(rack).sortedByDescending { it.score }
        val highestScoringMove = sortedByDescending.firstOrNull()
        val highestScoringMoveWithoutE = sortedByDescending
            .firstOrNull {
                it.addedE()
            }
        return if (highestScoringMoveWithoutE != null && highestScoringMoveWithoutE.score > (highestScoringMove!!.score - 3)) {
            highestScoringMoveWithoutE
        } else {
            highestScoringMove
        }
    }

    private fun Move.addedE(): Boolean {
        return this.addedTiles.map { it.first.letter }.equals('E')
    }

    fun getNextHighestScoringMove(board: Board, rack: Rack): Move? {
        val sortedByDescending = board.findAllMoves(rack).sortedByDescending { it.score }
        return if (sortedByDescending.size > 1) {
            sortedByDescending[1]
        } else {
            sortedByDescending.firstOrNull()
        }
    }

}