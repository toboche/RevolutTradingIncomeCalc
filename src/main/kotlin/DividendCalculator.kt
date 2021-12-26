import java.math.BigDecimal
import java.util.*

class DividendCalculator {
    fun calculateDividendTax(
        allTransactions: List<Transaction>,
        dividendTaxRatePercentAlreadyPaidInUsa: BigDecimal,
        totalDividendTaxRatePercent: BigDecimal,
        dateRange: ClosedRange<GregorianCalendar>,
    ) = allTransactions.filter { it.type == TransactionType.DIVIDEND }
        .filter { dateRange.contains(it.date) }
        .sumOf {
            //                12.55*1/(1-0.15)
            val grossDividend =
                it.totalAmount * BigDecimal.ONE / (BigDecimal.ONE - dividendTaxRatePercentAlreadyPaidInUsa)
            val alreadyPaidTaxInUsa = grossDividend - it.totalAmount
            val totalDividendTax = grossDividend * totalDividendTaxRatePercent
            (totalDividendTax - alreadyPaidTaxInUsa)
        }

}
