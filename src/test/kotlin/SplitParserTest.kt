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
                mapOf(
                    "ANET" to
                            SplitParser.Split(
                                LocalDate(
                                    2021, 11, 18
                                ),
                                BigDecimal("4")
                            )
                )
            )
    }
}