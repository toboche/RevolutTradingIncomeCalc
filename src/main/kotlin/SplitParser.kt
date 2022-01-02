import kotlinx.datetime.LocalDate
import java.math.BigDecimal

class SplitParser {
    fun parse(input: String): List<Split> =
        input.lines()
            .drop(1)
            .map {
                it.split(",")
                    .let { substrings ->
                        val dateString = substrings[0]
                        val tickerString = substrings[1]
                        val ratio = BigDecimal(substrings[2])
//                        2021-11-18
                        val date = LocalDate(dateString.substring(0, 4).toInt(),
                            dateString.substring(5, 7).toInt(),
                            dateString.substring(8, 10).toInt()
                        )
                        Split(
                            tickerString,
                            date,
                            ratio
                        )
                    }
            }

    data class Split(
        val ticker: String,
        val date: LocalDate,
        val ratio: BigDecimal,
    )
}