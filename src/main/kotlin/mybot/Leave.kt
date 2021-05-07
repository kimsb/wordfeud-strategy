package mybot


fun leaveScore(leave: String): Int {
    val letterCounts = mutableMapOf<Char, Int>()
    var score = 0
    leave.toCharArray().forEach {
        letterCounts[it] = 1 + (letterCounts[it] ?: 0)

        val indexOf = eranstlikList.indexOf(it.toString().repeat(letterCounts[it]!!))

        score += (if (indexOf != -1) indexOf else 100)
    }
    return score / leave.length
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