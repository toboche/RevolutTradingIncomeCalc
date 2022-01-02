import kotlinx.datetime.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

internal class CapitalGainCalculatorTest {
    @Test
    internal fun `calculate my 2021 dividend tax`() {
        val result = CapitalGainCalculator()
            .calculate(
                File("src/test/resources/sample2.csv").readText(),
                LocalDate(2021, 1, 1),
                LocalDate(2021, 12, 31),
                File("src/main/resources/stockSplits.csv").readText(),
            )
        assertThat(result.finallyToPay).isCloseTo(
            BigDecimal("277.9945570846459176"),
            Offset.offset(BigDecimal("0.01"))
        )
        assertThat(result.finalLoss).isEqualTo(ZERO)

//        dividendTaxLeftToPay = {BigDecimal@3580} "20.58496419"
//        custodyFees = {BigDecimal@3581} "71.505504"
//        tickerGainCalculationResult = {TickerBalanceCalculator$Loss@3582} Loss(loss=-8507.14995707808376)
//        finallyToPay = {BigDecimal@3583} "0"
//        finalLoss = {BigDecimal@3584} "-50.92053981"


//        result = {CapitalGainCalculator$GainAndExpenses@3287} GainAndExpenses(dividendTaxLeftToPay=20.58496419, custodyFees=71.505504, tickerGainCalculationResult=GainTax(tax=328.9150968946459176), finallyToPay=277.9945570846459176, finalLoss=0)
//        dividendTaxLeftToPay = {BigDecimal@3547} "20.58496419"
//        custodyFees = {BigDecimal@3548} "71.505504"
//        tickerGainCalculationResult = {TickerBalanceCalculator$GainTax@3549} GainTax(tax=328.9150968946459176)
//        finallyToPay = {BigDecimal@3550} "277.9945570846459176"
//        finalLoss = {BigDecimal@3551} "0"
    }
}