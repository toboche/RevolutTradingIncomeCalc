import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

class DividendCalculator {
    fun calculateDividendTax(
        allTransactions: List<Transaction>,
        dividendTaxRatePercentAlreadyPaidInUsa: BigDecimal,
        totalDividendTaxRatePercent: BigDecimal,
        dateRange: ClosedRange<LocalDate>,
    ) = allTransactions.filter { it.type == TransactionType.DIVIDEND }
        .filter { dateRange.contains(it.date) }
        .fold(
            DividendTax(
                ZERO,
                ZERO,
                ZERO,
                ZERO
            )
        ) { acc, transaction ->
            //                12.55*1/(1-0.15)
            val grossDividend =
                transaction.totalAmount * BigDecimal.ONE / (BigDecimal.ONE - dividendTaxRatePercentAlreadyPaidInUsa)
            val alreadyPaidTaxInUsa = grossDividend - transaction.totalAmount
            val totalDividendTax = grossDividend * totalDividendTaxRatePercent
            acc.copy(
                alreadyPaidTax = acc.alreadyPaidTax + alreadyPaidTaxInUsa,
                totalTaxToPay = acc.totalTaxToPay + totalDividendTax,
                leftTaxToPay = acc.leftTaxToPay + (totalDividendTax - alreadyPaidTaxInUsa),
                netIncome = acc.netIncome + transaction.totalAmount + alreadyPaidTaxInUsa
            )
        }

    data class DividendTax(
        val alreadyPaidTax: BigDecimal,
        val totalTaxToPay: BigDecimal,
        val leftTaxToPay: BigDecimal,
        val netIncome: BigDecimal,
    )
}
