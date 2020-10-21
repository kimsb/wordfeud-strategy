import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import wordfeudapi.domain.Tile
import java.util.*

class WordfeudTestKtTest {

    @Test
    fun testCrossChecks() {

        //TODO init av denne kun én gang
        Dictionary.initialize()

        val squares = crossChecksForRow(createRow("-----BJØRN-BÆR-"))

        assertThat(squares[0].crossChecks).`as`("Her skulle alle bits vært satt").isEqualTo(fullBitSet())
        assertThat(squares[1].crossChecks).`as`("Her skulle alle bits vært satt").isEqualTo(fullBitSet())
        assertThat(squares[2].crossChecks).`as`("Her skulle alle bits vært satt").isEqualTo(fullBitSet())
        assertThat(squares[3].crossChecks).`as`("Her skulle alle bits vært satt").isEqualTo(fullBitSet())
        assertThat(squares[4].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[5].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[6].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[7].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[8].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[9].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        val e = BitSet(26)
        e.flip(4)
        assertThat(squares[10].crossChecks).`as`("Her skulle bare bit e (index 4) vært satt").isEqualTo(e)
        assertThat(squares[11].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[12].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        assertThat(squares[13].crossChecks).`as`("Her skulle ingen bits vært satt").isEqualTo(BitSet(26))
        val ae = BitSet(26)
        ae.flip(0)
        ae.flip(4)
        assertThat(squares[14].crossChecks).`as`("Her skulle bit a og e (index 0 og 4) vært satt").isEqualTo(ae)
    }

    @Test
    fun testCrossSum() {
        Dictionary.initialize()

        val squares = crossChecksForRow(createRow("-----BJØRN-BÆR-"))

        assertThat(squares[0].crossSum).`as`("Her skulle crossSum vært 0 (har ingen prefix eller suffix)").isEqualTo(0)
        assertThat(squares[1].crossSum).`as`("Her skulle crossSum vært 0 (har ingen prefix eller suffix)").isEqualTo(0)
        assertThat(squares[2].crossSum).`as`("Her skulle crossSum vært 0 (har ingen prefix eller suffix)").isEqualTo(0)
        assertThat(squares[3].crossSum).`as`("Her skulle crossSum vært 0 (har ingen prefix eller suffix)").isEqualTo(0)
        assertThat(squares[4].crossSum).`as`("Her skulle crossSum vært 14 (for BJØRN)").isEqualTo(14)
        assertThat(squares[5].crossSum).`as`("Her skulle crossSum vært 0 (feltet er allerede brukt)").isEqualTo(0)
        assertThat(squares[6].crossSum).`as`("Her skulle crossSum vært 0 (feltet er allerede brukt)").isEqualTo(0)
        assertThat(squares[7].crossSum).`as`("Her skulle crossSum vært 0 (feltet er allerede brukt)").isEqualTo(0)
        assertThat(squares[8].crossSum).`as`("Her skulle crossSum vært 0 (feltet er allerede brukt)").isEqualTo(0)
        assertThat(squares[9].crossSum).`as`("Her skulle crossSum vært 0 (feltet er allerede brukt)").isEqualTo(0)
        assertThat(squares[10].crossSum).`as`("Her skulle crossSum vært 27 (14 for BJØRN + 13 for BÆR)").isEqualTo(27)
        assertThat(squares[11].crossSum).`as`("Her skulle crossSum vært 0 (feltet er allerede brukt)").isEqualTo(0)
        assertThat(squares[12].crossSum).`as`("Her skulle crossSum vært 0 (feltet er allerede brukt)").isEqualTo(0)
        assertThat(squares[13].crossSum).`as`("Her skulle crossSum vært 0 (feltet er allerede brukt)").isEqualTo(0)
        assertThat(squares[14].crossSum).`as`("Her skulle crossSum vært 13 (for BÆR)").isEqualTo(13)
    }

    private fun createRow(tiles: String): Row {
        return Row(tiles.map { if (it == '-') Square() else Square(tile = Tile(1, 1, it, false)) })
    }

    private fun fullBitSet(): BitSet {
        val bitSet = BitSet(26)
        bitSet.flip(0, 26)
        return bitSet
    }
}


