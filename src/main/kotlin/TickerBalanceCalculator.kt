import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

class TickerTaxCalculator {
    fun calculateTickerTax(
        allTransactions: List<Transaction>,
        taxRatePercent: BigDecimal,
        dateRange: ClosedRange<LocalDate>,
    ) = allTransactions.filter {
        it.type == TransactionType.BUY
                || it.type == TransactionType.SELL
    }
        .fold(mapOf<String, List<Transaction>>()) { state, transaction ->
            val tickerState = state.getOrDefault(transaction.ticker!!, emptyList())
            val newTickerState = tickerState + if (transaction.type == TransactionType.BUY) {
                tickerState + transaction
            } else {
                var quantityLeft = transaction.quantity!!
                var totalAmountPaidForSoldTicker = ZERO
                tickerState.map { historicalTicker ->
                    if (quantityLeft == ZERO) {
                        historicalTicker
                    } else if (quantityLeft >= historicalTicker.quantity) {
                        quantityLeft -= historicalTicker.quantity!!
                        totalAmountPaidForSoldTicker += historicalTicker.totalAmount
                        historicalTicker.copy(quantity = ZERO)
                    } else { //quantityLeft<historicalTicker.quantity
                        val amountPaidForSoldTicker =
                            (quantityLeft / historicalTicker.quantity!!) * historicalTicker.totalAmount
                        totalAmountPaidForSoldTicker += amountPaidForSoldTicker
                        quantityLeft = ZERO
                        historicalTicker.copy(quantity = historicalTicker.quantity - quantityLeft,
                            totalAmount = historicalTicker.totalAmount - amountPaidForSoldTicker)
                    }
                } + transaction.copy(income = transaction.totalAmount - totalAmountPaidForSoldTicker)
            }
            state + (transaction.ticker to newTickerState)
        }
        .map { it.value.sumOf { it.income ?: ZERO } }
        .sumOf { it } * taxRatePercent

    sealed class TransientTransaction
    data class Buy(
        val value: BigDecimal,
        val amount: BigDecimal,
    ) : TransientTransaction()
//        .groupBy { it.ticker!! }


    //        .filter { dateRange.contains(it.date) }
//        .sumOf {
//            //                12.55*1/(1-0.15)
//            val taxToPay =
//                it.totalAmount * taxRatePercent
//            val alreadyPaidTaxInUsa = grossDividend - it.totalAmount
//            val totalDividendTax = grossDividend * totalDividendTaxRatePercent
//            (totalDividendTax - alreadyPaidTaxInUsa)
//        }

}