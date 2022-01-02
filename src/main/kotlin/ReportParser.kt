import kotlinx.datetime.LocalDate
import java.math.BigDecimal

class ReportParser {
    fun parse(input: String): List<Transaction> {
        return input.lines()
            .drop(1)
            .filter { it.isNotBlank() }
            .map {
                val split = it.split(",")
//                02/12/2019 17:35:00
                val date = LocalDate(split[0].substring(6, 10).toInt(),
                    split[0].substring(3, 5).toInt(),
                    split[0].substring(0, 2).toInt()
                )
                val ticker = split[1].let { it.ifEmpty { null } }
                val type = TransactionType.values().find { it.mappedName == split[2] }!!
                val quantity = split[3].let { if (it.isEmpty()) null else BigDecimal(it) }
                val pricePerShare = split[4].let { if (it.isEmpty()) null else BigDecimal(it) }
                val totalAmount = BigDecimal(split[5])
                val currency = split[6]
                val fxRate = BigDecimal(split[7])
                Transaction(
                    date,
                    ticker,
                    type,
                    quantity,
                    totalAmount,
                    currency,
                    fxRate
                )
            }
    }
}