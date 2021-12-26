import java.math.BigDecimal

data class Transaction(
    val date: String,
    val ticker: String?,
    val type: TransactionType,
    val quantity: BigDecimal?,
    val pricePerShare: BigDecimal?,
    val totalAmount: BigDecimal,
    val currency: String,
    val fxRate: BigDecimal,
)
