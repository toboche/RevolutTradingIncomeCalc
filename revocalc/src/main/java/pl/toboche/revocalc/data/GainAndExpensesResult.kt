package pl.toboche.revocalc.data

import CapitalGainCalculator
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class GainAndExpensesResult(
    val dividendTaxLeftToPay: BigDecimal,
    val dividendTaxAlreadyPaid: BigDecimal,
    val totalDividendTaxToPay: BigDecimal,
    val custodyFees: BigDecimal,
    val tickerGainCalculationResult: BigDecimal,
    val finallyToPay: BigDecimal,
    val finalLoss: BigDecimal,
    val tradingIncome: BigDecimal,
    val tradingCost: BigDecimal,
    val totalGrossIncome: BigDecimal,
) : Parcelable {
    constructor(gainAndExpenses: CapitalGainCalculator.GainAndExpenses) : this(
        gainAndExpenses.dividendTaxLeftToPay,
        gainAndExpenses.dividendTaxAlreadyPaid,
        gainAndExpenses.totalDividendTaxToPay,
        gainAndExpenses.custodyFees,
        gainAndExpenses.tickerGainCalculationResult,
        gainAndExpenses.finallyToPay,
        gainAndExpenses.finalLoss,
        gainAndExpenses.tradingIncome,
        gainAndExpenses.tradingCost,
        gainAndExpenses.totalGrossIncome,
    )
}