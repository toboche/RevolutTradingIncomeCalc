import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.util.*

internal class DividendCalculatorTest {
    @Test
    internal fun `calculate sample dividend`() {
        val transactions = Parser().parse(File("src/test/resources/sample.csv").readText())

        val actual = DividendCalculator().calculateDividendTax(
            transactions,
            BigDecimal("0.15"),
            BigDecimal("0.19"),
            GregorianCalendar(2020, 0, 1).rangeTo(GregorianCalendar(2020, 11, 31))
        )

        Assertions.assertThat(actual).isEqualTo(BigDecimal("4.1713"))
    }
}