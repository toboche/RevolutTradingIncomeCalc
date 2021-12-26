import java.io.File
import java.math.BigDecimal
import java.util.*

class IncomeCalculator {
    fun calculateIncome(
        filename: String,
        dividendTaxRatePercentAlreadyPaidInUsa: BigDecimal = BigDecimal("0.15"),
        totalDividendTaxRatePercent: BigDecimal = BigDecimal("0.19"),
        startingDate: GregorianCalendar,
        endingDate: GregorianCalendar,
    ) {
        val dateRange = startingDate.rangeTo(endingDate)
        val parser = Parser()
        val allTransactions = parser.parse(File(filename).readText())

        val dividendTax = DividendCalculator().calculateDividendTax(allTransactions,
            dividendTaxRatePercentAlreadyPaidInUsa,
            totalDividendTaxRatePercent,
            dateRange)
    }

}