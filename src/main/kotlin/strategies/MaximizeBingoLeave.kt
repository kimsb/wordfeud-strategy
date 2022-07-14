package strategies

import domain.Game
import domain.Move
import domain.Rack
import strategies.StrategyName.MAXIMIZE_BINGO_LEAVE

class MaximizeBingoLeave(override val weight: Double) : WeightedStrategy {

    override val name = MAXIMIZE_BINGO_LEAVE

    override fun relativeScore(game: Game, allMovesSorted: List<Move>): List<Double> {
        if (allMovesSorted.isEmpty()) {
            return emptyList()
        }

        val leaveScores = allMovesSorted.map { move ->
            leaveScore(rackAfterMove(game.rack, move))
        }
        val min = leaveScores.minOrNull()!!
        val max = leaveScores.maxOrNull()!!

        return leaveScores.map {
            weight * relativeScore(internalScore = it, min = min, max = max)
        }
    }

    private fun relativeScore(internalScore: Int, min: Int, max: Int): Double {
        return when (internalScore) {
            max -> 1.0
            min -> 0.0
            else -> internalScore / (min + max).toDouble()
        }
    }

    private fun rackAfterMove(rack: Rack, move: Move): Rack {
        var rackAfterMove = rack
        move.addedTiles.map { it.first.letter }.forEach {
            rackAfterMove = if (it.isLowerCase()) {
                rackAfterMove.without('*')
            } else {
                rackAfterMove.without(it)
            }
        }
        return rackAfterMove
    }

    fun leaveScore(rackAfterMove: Rack): Int {
        //Dropper å gi leave-score for blank, siden denne allerede blir behandlet i dont use blank-strategien
        val rackWithoutBlank = if (rackAfterMove.contains('*')) rackAfterMove.without('*') else rackAfterMove
        if (rackWithoutBlank.tiles.isEmpty()) {
            return 95
        }
        val letterCounts = mutableMapOf<Char, Int>()
        var score = 0

        rackWithoutBlank.tiles.forEach {
            letterCounts[it] = 1 + (letterCounts[it] ?: 0)

            val indexOf = eranstlikList.indexOf(it.toString().repeat(letterCounts[it]!!))

            score += (if (indexOf != -1) indexOf else 100)
        }

        //penalty for bad vowelRatio //TODO teste denne penaltyen... burde det være en egen strategi?
        //return 100 - (score / rackAfterMove.tiles.size + (if (vowelRatioOK(rackAfterMove.tiles)) 0 else 20))
        return 100 - (score / rackWithoutBlank.tiles.size)
    }

    private val eranstlikList =
        listOf(
            "E",
            "R",
            "N",
            "A",
            "T",
            "S",
            "L",
            "I",
            "K",
            "EE",
            "O",
            "G",
            "D",
            "U",
            "M",
            "V",
            "B",
            "P",
            "F",
            "Ø",
            "Y",
            "H",
            "TT",
            "Å",
            "RR",
            "NN",
            "AA",
            "SS",
            "J",
            "LL",
            "KK",
            "EEE",
            "II",
            "GG",
            "Æ",
            "DD",
            "PP",
            "OO",
            "C",
            "MM",
            "BB",
            "UU",
            "TTT",
            "VV",
            "FF",
            "NNN",
            "AAA",
            "SSS",
            "ØØ",
            "W",
            "RRR",
            "KKK",
            "ÅÅ",
            "GGG",
            "LLL",
            "HH",
            "DDD",
            "OOO",
            "TTTT",
            "EEEE",
            "III",
            "SSSS",
            "JJ",
            "MMM",
            "BBB",
            "FFF",
            "UUU",
            "NNNN",
            "VVV",
            "LLLL",
            "GGGG",
            "AAAA",
        )

}