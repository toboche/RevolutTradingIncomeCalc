import kotlinx.datetime.LocalDate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal

internal class DividendCalculatorTest {
    @Test
    internal fun `calculate sample dividend`() {
        val transactions = ReportParser().parse(File("src/test/resources/sample.csv").readText())

        val actual = DividendCalculator().calculateDividendTax(
            transactions,
            BigDecimal("0.15"),
            BigDecimal("0.19"),
            LocalDate(2020, 1, 1).rangeTo(LocalDate(2020, 12, 31))
        )

        Assertions.assertThat(actual).isEqualTo(
            DividendCalculator.DividendTax(
                alreadyPaidTax = 15.64.toBigDecimal(),
                totalTaxToPay = 19.8113.toBigDecimal(),
                leftTaxToPay = 4.1713.toBigDecimal()
            )
        )
    }

    @Test
    internal fun `calculate sample dividend for 2021`() {
        val transactions = ReportParser().parse(File("src/test/resources/sample.csv").readText())

        val actual = DividendCalculator().calculateDividendTax(
            transactions,
            BigDecimal("0.15"),
            BigDecimal("0.19"),
            LocalDate(2021, 1, 1).rangeTo(LocalDate(2021, 12, 31))
        )

        Assertions.assertThat(actual).isEqualTo(
            DividendCalculator.DividendTax(
                alreadyPaidTax = 19.94.toBigDecimal(),
                totalTaxToPay = 25.27.toBigDecimal().setScale(4),
                leftTaxToPay = 5.33.toBigDecimal().setScale(4)
            )
        )
    }
}