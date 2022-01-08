import kotlinx.datetime.LocalDate
import org.jetbrains.annotations.TestOnly
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode

class TickerBalanceCalculator(
    private val taxRatePercent: BigDecimal = BigDecimal("0.19"),
) {

    @TestOnly
    fun calculateSumOfTickerTaxes(
        allTransactions: List<Transaction>,
        dateRange: ClosedRange<LocalDate>,
        splits: List<SplitParser.Split>,
    ) = allTransactions.filter {
        it.type == TransactionType.BUY
                || it.type == TransactionType.SELL
    }
        .fold(mapOf<String, List<Transaction>>()) { state, transaction ->
            val tickerState = state.getOrDefault(transaction.ticker!!, emptyList())
            val newTickerState = if (transaction.type == TransactionType.BUY) {
                listOf(transaction) + state.getOrDefault(transaction.ticker, emptyList())
            } else {
                var quantityLeftToBuy = transaction.quantity!!
                var totalAmountPaidForSoldTicker = ZERO
                tickerState.reversed().map { historicalTicker ->
                    if (quantityLeftToBuy == ZERO || historicalTicker.type != TransactionType.BUY) {
                        historicalTicker
                    } else if (quantityLeftToBuy >= historicalTickerQuantityIncludingPossibleSplits(
                            historicalTicker,
                            splits,
                            transaction
                        )
                    ) {
                        quantityLeftToBuy -= historicalTickerQuantityIncludingPossibleSplits(
                            historicalTicker,
                            splits,
                            transaction
                        )
                        totalAmountPaidForSoldTicker += historicalTicker.quantity!! * historicalTicker.pricePerShare!!
                        historicalTicker.copy(quantity = ZERO)
                    } else { //quantityLeftToBuy<historicalTicker.quantity
                        val splitsRatio = splitsRatio(
                            splits,
                            historicalTicker,
                            transaction
                        )
                        val pricePerUnit = historicalTicker.pricePerShare!! / splitsRatio
                        val amountPaidForSoldTicker =
                            pricePerUnit * quantityLeftToBuy
                        totalAmountPaidForSoldTicker += amountPaidForSoldTicker
                        val justBoughtQuantity = quantityLeftToBuy
                        quantityLeftToBuy = ZERO
                        historicalTicker.copy(quantity = historicalTicker.quantity!! - justBoughtQuantity / splitsRatio)
                    }
                }.reversed() + transaction.copy(
                    gain = transaction.totalAmount - totalAmountPaidForSoldTicker,
                    income = transaction.totalAmount,
                    costOfGettingIncome = totalAmountPaidForSoldTicker
                )
            }
            state + (transaction.ticker to newTickerState)
        }

//        .mapValues { it.value.filter { dateRange.contains(it.date) && it.type == TransactionType.SELL } }
//        .map { entry ->
//            entry.value.sumOf {
//                it.gain?.setScale(2, RoundingMode.HALF_UP) ?: ZERO
//            }
//        }
//        .sumOf { it }

    private fun historicalTickerQuantityIncludingPossibleSplits(
        historicalTicker: Transaction,
        splits: List<SplitParser.Split>,
        currentSellTransaction: Transaction,
    ) =
        splitsRatio(splits, historicalTicker, currentSellTransaction) * historicalTicker.quantity!!

    private fun splitsRatio(
        splits: List<SplitParser.Split>,
        historicalTicker: Transaction,
        currentSellTransaction: Transaction,
    ) = if (splits.any { it.ticker == historicalTicker.ticker }) {
        splits.filter {
            it.ticker == currentSellTransaction.ticker && historicalTicker.date.rangeTo(
                currentSellTransaction.date
            )
                .contains(it.date)
        }
            .fold(BigDecimal.ONE) { acc, split ->
                acc * split.ratio
            }
    } else {
        BigDecimal.ONE
    }

    fun calculateResult(
        allTransactions: List<Transaction>,
        dateRange: ClosedRange<LocalDate>,
        splits: List<SplitParser.Split>,
    ) =
        calculateSumOfTickerTaxes(
            allTransactions,
            dateRange,
            splits
        )
            .mapValues { it.value.filter { dateRange.contains(it.date) } }
            .flatMap { it.value }
            .fold(
                CalculationResult(
                    ZERO,
                    ZERO,
                    ZERO,
                    ZERO
                )
            ) { acc, transaction ->
                if (transaction.type == TransactionType.SELL) {
                    acc.copy(
                        grossIncome = acc.grossIncome + transaction.income!!,
                        costs = acc.costs + transaction.costOfGettingIncome!!
                    )
                } else {
                    acc
                }
            }
            .let {
                val gain = it.grossIncome - it.costs
                val tax = if (gain < ZERO) {
                    ZERO
                } else {
                    (taxRatePercent * gain).setScale(2, RoundingMode.HALF_UP)
                }
                val loss =
                    if (gain < ZERO) {
                        gain
                    } else {
                        ZERO
                    }
                it.copy(
                    tax = tax,
                    loss = loss
                )
            }

    data class CalculationResult(
        val tax: BigDecimal,
        val grossIncome: BigDecimal,
        val loss: BigDecimal,
        val costs: BigDecimal
    )
}