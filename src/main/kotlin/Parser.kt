import java.math.BigDecimal

class Parser {
    fun parse(input: String): List<Transaction> {
        return input.lines()
            .drop(1)
            .map {
                val split = it.split(",")
                val date = split[0]
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
                    pricePerShare,
                    totalAmount,
                    currency,
                    fxRate
                )
            }
    }
}