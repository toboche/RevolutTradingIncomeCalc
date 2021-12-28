import kotlinx.datetime.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal

internal class ParserTest {

    @Test
    fun `parse simple data`() {
        val input = "Date,Ticker,Type,Quantity,Price per share,Total Amount,Currency,FX Rate\n" +
                "02/12/2019 17:35:00,,CASH TOP-UP,,,800.00,USD,0.258499522"

        val actual = Parser().parse(input)

        assertThat(actual)
            .isEqualTo(listOf(Transaction(
                LocalDate(2019, 12, 2),
                null,
                TransactionType.CASH_TOP_UP,
                null,
                null,
                BigDecimal("800.00"),
                "USD",
                BigDecimal("0.258499522")
            )))
    }

    @Test
    fun `parse sample`() {
        val input = File("src/test/resources/sample.csv").readText()

        val actual = Parser().parse(input)

        assertThat(actual)
            .hasSize(88)
    }
}