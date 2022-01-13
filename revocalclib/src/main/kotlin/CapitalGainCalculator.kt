import api.nbp.ExchangeRateLoader
import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode

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
        val capitalGainTax = capitalGain.tax
        val finallyToPay = (-custodyFees + capitalGainTax + dividendTax.leftTaxToPay).let {
            if (it > ZERO) it
            else ZERO
        }
        val finalLoss = if (finallyToPay > ZERO) {
            ZERO
        } else {
            -custodyFees + capitalGainTax + dividendTax.leftTaxToPay
        }

        val tickerGainCalculationResult = capitalGain.tax

        return GainAndExpenses(
            dividendTaxLeftToPay = dividendTax.leftTaxToPay.setScale(0, RoundingMode.HALF_UP),
            dividendTaxAlreadyPaid = dividendTax.alreadyPaidTax,
            totalDividendTaxToPay = dividendTax.totalTaxToPay,
            custodyFees = custodyFees,
            tickerGainCalculationResult = tickerGainCalculationResult,
            finallyToPay = finallyToPay,
            finalLoss = finalLoss,
            tradingIncome = capitalGain.grossIncome,
            tradingCost = capitalGain.costs + custodyFees,
            totalGrossIncome = capitalGain.grossIncome + dividendTax.grossIncome,
        )
    }

    data class GainAndExpenses(
        val dividendTaxLeftToPay: BigDecimal, //not put anywhere- this should be automaticall calculated once the below two fields are provided. This should be rounded to full zlotys
        val dividendTaxAlreadyPaid: BigDecimal, //w sekcje „KWOTA DO ZAPŁATY / NADPŁATA”, pole: „Podatek zapłacony za granicą, o którym mowa w art. 30a ust. 9 ustawy”.
        val totalDividendTaxToPay: BigDecimal, //w sekcje „KWOTA DO ZAPŁATY / NADPŁATA”, pole: „Zryczałtowany podatek obliczony od przychodów (dochodów), o których mowa w art. 30a ust. 1 pkt 1–5 ustawy, uzyskanych poza granicami Rzeczypospolitej Polskiej”.
        val custodyFees: BigDecimal, //not put anywhere - this is part of your expenses
        val tickerGainCalculationResult: BigDecimal,
        val finallyToPay: BigDecimal,
        val finalLoss: BigDecimal,
        //TODO dodac przychod oraz koszty uzyskania przychodow, potrzebne do pit-38
        //https://bezprawnik.pl/jak-rozliczyc-akcje-revolut/
        val tradingIncome: BigDecimal,
        val tradingCost: BigDecimal,
        val totalGrossIncome: BigDecimal,
    )
}