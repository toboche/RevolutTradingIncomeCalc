package api.nbp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NbpService {
    @GET("api/exchangerates/rates/a/gbp/{year}-{month}-{day}/?format=json")
    fun listRepos(
        @Path("year") year: String?,
        @Path("month") month: String?,
        @Path("day") day: String?,
    ): Call<RatesResponse>
}