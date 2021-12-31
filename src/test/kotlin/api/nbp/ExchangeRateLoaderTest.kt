package api.nbp

import kotlinx.datetime.LocalDate
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ExchangeRateLoaderTest {
    private val server = MockWebServer()

    @Test
    internal fun `check exchange rate for date`() {
        server.enqueue(MockResponse().setBody("{\"table\":\"A\",\"currency\":\"funt szterling\",\"code\":\"USD\",\"rates\":[{\"no\":\"1/A/NBP/2012\",\"effectiveDate\":\"2012-01-02\",\"mid\":5.3480}]}"))

        val actual = ExchangeRateLoader(server.url("/").toUrl().toString())
            .load(LocalDate(2012, 1, 2))

        val request = server.takeRequest()
        Assertions.assertThat(actual).isEqualTo(BigDecimal("5.3480"))
        Assertions.assertThat(request.path).isEqualTo(
            "/api/exchangerates/rates/a/usd/2012-01-02/?format=json"
        )
    }
}