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

        Assertions.assertThat(actual).isEqualTo(BigDecimal("4.1713"))
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

        Assertions.assertThat(actual).isEqualTo(BigDecimal("5.3300"))
    }
}