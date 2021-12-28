import kotlinx.datetime.LocalDate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal

internal class TickerTaxCalculatorTest {
    private val startDate = LocalDate(2020, 1, 1)

    private val endDate = LocalDate(2020, 12, 31)

    @Test
    internal fun `calculate zero tax for one buy`() {
        val transactions = listOf(
            transaction(
                TransactionType.BUY,
                quantity = BigDecimal("1"),
                totalAmount = BigDecimal("1"),
            ),
        )

        val actual = TickerTaxCalculator().calculateTickerTax(
            transactions,
            BigDecimal("0.19"),
            startDate.rangeTo(endDate),
        )

        Assertions.assertThat(actual).isZero
    }

    @Test
    internal fun `calculate tax for one buy and one sell`() {
        val transactions = listOf(
            transaction(
                TransactionType.BUY,
                quantity = BigDecimal("1"),
                totalAmount = BigDecimal("1"),
            ),
            transaction(
                TransactionType.SELL,
                quantity = BigDecimal("1"),
                totalAmount = BigDecimal("2"),
            )
        )

        val actual = TickerTaxCalculator().calculateTickerTax(
            transactions,
            BigDecimal("0.19"),
            startDate.rangeTo(endDate),
        )

        Assertions.assertThat(actual).isEqualTo(BigDecimal("0.19"))
    }

    @Test
    internal fun `calculate tax for one buy and half sell`() {
        val transactions = listOf(
            transaction(
                TransactionType.BUY,
                quantity = BigDecimal("1"),
                totalAmount = BigDecimal("1"),
            ),
            transaction(
                TransactionType.SELL,
                quantity = BigDecimal("0.5"),
                totalAmount = BigDecimal("1"),
            )
        )

        val actual = TickerTaxCalculator().calculateTickerTax(
            transactions,
            BigDecimal("0.19"),
            startDate.rangeTo(endDate),
        )

        Assertions.assertThat(actual).isEqualTo(BigDecimal("0.095"))
    }

    @Test
    internal fun `calculate sample for 2019`() {
        val transactions = Parser().parse(File("src/test/resources/sample.csv").readText())

        val actual = TickerTaxCalculator().calculateTickerTax(
            transactions,
            BigDecimal("0.19"),
            LocalDate(2019, 1, 1).rangeTo(LocalDate(2019, 12, 31)),
        )

        Assertions.assertThat(actual).isZero
    }

    private fun transaction(
        transactionType: TransactionType,
        quantity: BigDecimal,
        totalAmount: BigDecimal,
        ticker: String = "TCK",
    ) =
        Transaction(startDate,
            ticker,
            transactionType,
            quantity,
            BigDecimal("1"),
            totalAmount,
            "PLN",
            BigDecimal("1")
        )
}