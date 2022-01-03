import kotlinx.datetime.LocalDate
import java.math.BigDecimal

data class Transaction(
    val date: LocalDate,
    val ticker: String?,
    val type: TransactionType,
    val quantity: BigDecimal?,
    val pricePerShare: BigDecimal?,
    val totalAmount: BigDecimal,
    val currency: String,
    val gain: BigDecimal? = null,
    val income: BigDecimal? = null,
    val costOfGettingIncome: BigDecimal? = null
)
