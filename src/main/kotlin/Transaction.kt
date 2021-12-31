import kotlinx.datetime.LocalDate
import java.math.BigDecimal

data class Transaction(
    val date: LocalDate,
    val ticker: String?,
    val type: TransactionType,
    val quantity: BigDecimal?,
    val totalAmount: BigDecimal,
    val currency: String,
    val fxRate: BigDecimal,
    val income: BigDecimal? = null,
)
