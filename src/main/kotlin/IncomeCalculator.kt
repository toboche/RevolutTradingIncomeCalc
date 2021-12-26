import java.io.File
import java.math.BigDecimal

class IncomeCalculator {
    fun calculateIncome(
        filename: String,
        dividendTaxRatePercentAlreadyPaidInUsa: BigDecimal = BigDecimal("0.15"),
        totalDividendTaxRatePercent: BigDecimal = BigDecimal("0.19"),
    ) {
        val parser = Parser()
        val allTransactions = parser.parse(File(filename).readText())

        val transactionToTaxToPay = allTransactions.filter { it.type == TransactionType.DIVIDEND }
            .associateWith {
//                12.55*1/(1-0.15)
                val alreadyPaidTaxInUsa =
                    it.totalAmount * BigDecimal.ONE * (BigDecimal.ONE - dividendTaxRatePercentAlreadyPaidInUsa)
                val totalDividendTax = it.totalAmount * totalDividendTaxRatePercent
                totalDividendTax - alreadyPaidTaxInUsa
            }

    }
}