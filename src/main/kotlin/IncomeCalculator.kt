import api.nbp.ExchangeRateLoader
import kotlinx.datetime.LocalDate
import java.io.File
import java.math.BigDecimal

class IncomeCalculator {
    fun calculateIncome(
        filename: String,
        dividendTaxRatePercentAlreadyPaidInUsa: BigDecimal = BigDecimal("0.15"),
        totalDividendTaxRatePercent: BigDecimal = BigDecimal("0.19"),
        startingDate: LocalDate,
        endingDate: LocalDate,
    ) {
        val dateRange = startingDate.rangeTo(endingDate)
        val parser = Parser()
        val exchangeRateLoader = ExchangeRateLoader()
        val allTransactions = parser.parse(File(filename).readText())
//            .map { it.copy(exchangeRateLoader.load()) }

        val dividendTax = DividendCalculator().calculateDividendTax(allTransactions,
            dividendTaxRatePercentAlreadyPaidInUsa,
            totalDividendTaxRatePercent,
            dateRange)
    }

}