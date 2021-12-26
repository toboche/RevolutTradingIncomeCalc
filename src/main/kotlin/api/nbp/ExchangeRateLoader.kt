package api.nbp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExchangeRateLoader(baseUrl: String = "https://api.nbp.pl/") {
    private var retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var service = retrofit.create(NbpService::class.java)

    fun load(year: String, month: String, day: String) =
        service.listRepos(year, month, day).execute().body()!!.rates.first().mid
}