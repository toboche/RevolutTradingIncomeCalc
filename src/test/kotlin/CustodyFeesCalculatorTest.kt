import kotlinx.datetime.LocalDate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal

internal class CustodyFeesCalculatorTest {
    @Test
    internal fun `calculate fees`() {
        val transactions = Parser().parse(File("src/test/resources/sample.csv").readText())

        val actual = CustodyFeesCalculator().calculate(
            transactions,
            LocalDate(2020, 1, 1).rangeTo(LocalDate(2020, 12, 31))
        )

        Assertions.assertThat(actual)
            .isEqualTo(BigDecimal("-1.01"))
    }
}