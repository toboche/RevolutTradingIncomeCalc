import api.nbp.ExchangeRateLoader
import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

class CapitalGainCalculator(
    private val dividendTaxRatePercentAlreadyPaidInUsa: BigDecimal = BigDecimal("0.15"),
    private val totalDividendTaxRatePercent: BigDecimal = BigDecimal("0.19"),
) {

    private val exchangeRateLoader = ExchangeRateLoader()

    fun calculate(
        input: String,
        startingDate: LocalDate,
        endingDate: LocalDate,
        splitInput: String,
    ): GainAndExpenses {
        val dateRange = startingDate.rangeTo(endingDate)
        val reportParser = ReportParser()
        val allTransactions = reportParser.parse(input)
            .map {
                it.copy(
                    totalAmount = it.totalAmount * exchangeRateLoader.load(
                        it.date
                    ),
                    pricePerShare =
                    if (it.pricePerShare != null) it.pricePerShare * exchangeRateLoader.load(
                        it.date
                    ) else
                        null
                )
            }
        val splits = SplitParser().parse(splitInput)

        val dividendTax = DividendCalculator().calculateDividendTax(
            allTransactions,
            dividendTaxRatePercentAlreadyPaidInUsa,
            totalDividendTaxRatePercent,
            dateRange
        )

        val custodyFees = -CustodyFeesCalculator()
            .calculate(
                allTransactions,
                dateRange
            )

        val capitalGain = TickerBalanceCalculator()
            .calculateResult(
                allTransactions,
                dateRange,
                splits
            )
        val capitalGainTax = when (capitalGain) {
            is TickerBalanceCalculator.GainTax -> {
                capitalGain.tax
            }
            else -> {
                ZERO
            }
        }
        val finallyToPay = (-custodyFees + capitalGainTax + dividendTax).let {
            if (it > ZERO) it
            else ZERO
        }
        val finalLoss = if (finallyToPay > ZERO) {
            ZERO
        } else {
            -custodyFees + capitalGainTax + dividendTax
        }

        val tickerGainCalculationResult = when (capitalGain) {
            is TickerBalanceCalculator.GainTax -> capitalGain.tax
            is TickerBalanceCalculator.Loss -> capitalGain.loss
        }

        return GainAndExpenses(
            dividendTax,
            custodyFees,
            tickerGainCalculationResult,
            finallyToPay,
            finalLoss
        )
    }

    data class GainAndExpenses(
        val dividendTaxLeftToPay: BigDecimal,
        val custodyFees: BigDecimal,
        val tickerGainCalculationResult: BigDecimal,
        val finallyToPay: BigDecimal,
        val finalLoss: BigDecimal,
    )
}