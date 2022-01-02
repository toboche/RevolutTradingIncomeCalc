import kotlinx.datetime.LocalDate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class SplitParserTest {
    @Test
    internal fun `parse splits`() {
        val input = "Date,Ticker,Ratio\n" +
                "2021-11-18,ANET,4"
        val actual = SplitParser().parse(input)
        Assertions.assertThat(actual)
            .isEqualTo(
                listOf(
                    SplitParser.Split(
                        "ANET",
                        LocalDate(
                            2021, 11, 18
                        ),
                        BigDecimal("4")
                    )
                )
            )
    }

    @Test
    internal fun `parse input`() {
        val actual = SplitParser().parse(
            this::class.java.classLoader.getResource("stockSplits.csv")!!.readText()
        )

        Assertions.assertThat(actual).hasSize(25)
    }
}