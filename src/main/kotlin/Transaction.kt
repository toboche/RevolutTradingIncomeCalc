import java.math.BigDecimal
import java.util.*

data class Transaction(
    val date: GregorianCalendar,
    val ticker: String?,
    val type: TransactionType,
    val quantity: BigDecimal?,
    val pricePerShare: BigDecimal?,
    val totalAmount: BigDecimal,
    val currency: String,
    val fxRate: BigDecimal,
)
