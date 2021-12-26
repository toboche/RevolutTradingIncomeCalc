import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

internal class ParserTest {

    @Test
    fun parse() {
        val input = "Date,Ticker,Type,Quantity,Price per share,Total Amount,Currency,FX Rate\n" +
                "02/12/2019 17:35:00,,CASH TOP-UP,,,800.00,USD,0.258499522"

        val actual = Parser().parse(input)

        Assertions.assertThat(actual)
            .isEqualTo(listOf(Transaction(
                GregorianCalendar(2019, 11, 2),
                null,
                TransactionType.CASH_TOP_UP,
                null,
                null,
                BigDecimal("800.00"),
                "USD",
                BigDecimal("0.258499522")
            )))
    }
}