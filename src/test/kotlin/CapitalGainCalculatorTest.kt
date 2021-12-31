import kotlinx.datetime.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

internal class CapitalGainCalculatorTest {
    @Test
    internal fun `calculate sample dividend tax`() {
        val result = CapitalGainCalculator()
            .calculate(
                File("src/test/resources/sample2.csv").readText(),
                LocalDate(2021, 1, 1),
                LocalDate(2021, 12, 31)
            )
        assertThat(result.finallyToPay).isCloseTo(
            BigDecimal("277.9945570846459176"),
            Offset.offset(BigDecimal("0.01"))
        )
        assertThat(result.finalLoss).isEqualTo(ZERO)
    }
}