package api.nbp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NbpService {
    @GET("api/exchangerates/rates/a/usd/{year}-{month}-{day}/?format=json")
    fun loadExchangeRate(
        @Path("year") year: Int,
        @Path("month") month: String,
        @Path("day") day: String,
    ): Call<RatesResponse>
}