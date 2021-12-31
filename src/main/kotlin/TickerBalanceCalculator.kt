import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

class TickerBalanceCalculator(
    private val taxRatePercent: BigDecimal = BigDecimal("0.19"),
) {

    fun calculateTickerTax(
        allTransactions: List<Transaction>,
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
        .mapValues { it.value.filter { dateRange.contains(it.date) } }
        .map { it.value.sumOf { it.income ?: ZERO } }
        .sumOf { it }

    fun calculateResult(
        allTransactions: List<Transaction>,
        dateRange: ClosedRange<LocalDate>,
    ) =
        calculateTickerTax(
            allTransactions,
            dateRange
        )
            .let {
                if (it < ZERO) {
                    Loss(it)
                } else {
                    GainTax(taxRatePercent * it)
                }
            }


    sealed class Result
    data class GainTax(
        val tax: BigDecimal,
    ) : Result()

    data class Loss(
        val loss: BigDecimal,
    ) : Result()
}