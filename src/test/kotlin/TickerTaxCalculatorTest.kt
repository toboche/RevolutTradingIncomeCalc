import kotlinx.datetime.LocalDate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class TickerTaxCalculatorTest {
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
            LocalDate(2020, 1, 1).rangeTo(LocalDate(2020, 12, 31)),
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
            LocalDate(2020, 1, 1).rangeTo(LocalDate(2020, 12, 31)),
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
            LocalDate(2020, 1, 1).rangeTo(LocalDate(2020, 12, 31)),
        )

        Assertions.assertThat(actual).isEqualTo(BigDecimal("0.095"))
    }

    private fun transaction(
        transactionType: TransactionType,
        quantity: BigDecimal,
        totalAmount: BigDecimal,
        ticker: String = "TCK",
    ) =
        Transaction(LocalDate(1, 1, 1),
            ticker,
            transactionType,
            quantity,
            BigDecimal("1"),
            totalAmount,
            "PLN",
            BigDecimal("1")
        )
}