import kotlinx.datetime.LocalDate

class CustodyFeesCalculator {
    fun calculate(
        allTransactions: List<Transaction>,
        dateRange: ClosedRange<LocalDate>,
    ) = allTransactions.filter { it.type == TransactionType.CUSTODY_FEE }
        .filter { dateRange.contains(it.date) }
        .sumOf {
            it.totalAmount
        }
}