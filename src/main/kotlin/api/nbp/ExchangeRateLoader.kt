package api.nbp

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal

class ExchangeRateLoader(baseUrl: String = "http://api.nbp.pl/") {
    private var retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val cache = mutableMapOf<LocalDate, BigDecimal>()

    private var service = retrofit.create(NbpService::class.java)

    fun load(date: LocalDate): BigDecimal {
        var result: Response<RatesResponse>
        if (cache[date] != null) {
            return cache[date]!!
        }
        var dateAdjusted = date
        do {
            val month = String.format("%02d", dateAdjusted.monthNumber)
            val day = String.format("%02d", dateAdjusted.dayOfMonth)
            result =
                service.loadExchangeRate(dateAdjusted.year, month, day).execute()
            dateAdjusted = dateAdjusted.minus(1, DateTimeUnit.DAY)
        } while (!result.isSuccessful)
        val toReturn = result.body()!!.rates.first().mid
        cache[date] = toReturn
        cache[dateAdjusted] = toReturn
        return toReturn

    }
}