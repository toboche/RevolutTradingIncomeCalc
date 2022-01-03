import kotlinx.datetime.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal

class TickerBalanceCalculatorTest {
    private val startDate = LocalDate(2020, 1, 1)

    private val endDate = LocalDate(2020, 12, 31)

    private val splits = emptyList<SplitParser.Split>()

    val splitsInput = "Date,Ticker,Type,Quantity,Pricepershare,TotalAmount,Currency\n" +
            "02-02-2021 15:45,NVDA,BUY,1,535.8,535.8,USD\n" +
            "02-07-2021 15:45,NVDA,SELL,0.25,816.07,204.0175,USD\n" +
            "20-07-2021 16:14,NVDA,SELL,0.25,206.71,51.6775,USD\n" +
            "19-11-2021 16:14,NVDA,SELL,2.75,329.85,907.0875,USD"

    @Test
    fun `calculate zero tax for one buy`() {
        val transactions = listOf(
            transaction(
                TransactionType.BUY,
                quantity = BigDecimal("1"),
                pricePerShare = BigDecimal("1"),
                totalAmount = BigDecimal("1"),
            ),
        )

        val actual = TickerBalanceCalculator().calculateResult(
            transactions,
            startDate.rangeTo(endDate),
            splits,
        ) as TickerBalanceCalculator.GainTax

        assertThat(actual.tax).isZero
    }

    @Test
    fun `calculate tax for one buy and one sell`() {
        val transactions = listOf(
            transaction(
                TransactionType.BUY,
                quantity = BigDecimal("1"),
                pricePerShare = BigDecimal("1"),
                totalAmount = BigDecimal("1"),
            ),
            transaction(
                TransactionType.SELL,
                quantity = BigDecimal("1"),
                pricePerShare = BigDecimal("2"),
                totalAmount = BigDecimal("2"),
            )
        )

        val actual = TickerBalanceCalculator().calculateResult(
            transactions,
            startDate.rangeTo(endDate),
            splits,
        ) as TickerBalanceCalculator.GainTax

        assertThat(actual.tax).isEqualTo(BigDecimal("0.19"))
    }

    @Test
    fun `calculate tax for one buy and half sell`() {
        val transactions = listOf(
            transaction(
                TransactionType.BUY,
                quantity = BigDecimal("1"),
                pricePerShare = BigDecimal("1"),
                totalAmount = BigDecimal("1"),
            ),
            transaction(
                TransactionType.SELL,
                quantity = BigDecimal("0.5"),
                pricePerShare = BigDecimal("2"),
                totalAmount = BigDecimal("1"),
            )
        )

        val actual = TickerBalanceCalculator().calculateResult(
            transactions,
            startDate.rangeTo(endDate),
            splits,
        ) as TickerBalanceCalculator.GainTax

        assertThat(actual.tax).isEqualTo(BigDecimal("0.10"))
    }

    @Test
    fun `calculate tax for one buy and half sell then buy and then sell`() {
        val transactions = listOf(
            transaction(
                TransactionType.BUY,
                quantity = BigDecimal("1"),
                pricePerShare = BigDecimal("1"),
                totalAmount = BigDecimal("1"),
            ),
            transaction(
                TransactionType.SELL,
                quantity = BigDecimal("0.5"),
                pricePerShare = BigDecimal("2"),
                totalAmount = BigDecimal("1"),
            ),
            transaction(
                TransactionType.BUY,
                quantity = BigDecimal("1"),
                pricePerShare = BigDecimal("3"),
                totalAmount = BigDecimal("3"),
            ),
            transaction(
                TransactionType.SELL,
                quantity = BigDecimal("1"),
                pricePerShare = BigDecimal("4"),
                totalAmount = BigDecimal("4"),
            ),
        )

        val actual = TickerBalanceCalculator(
            BigDecimal.ONE
        ).calculateResult(
            transactions,
            startDate.rangeTo(endDate),
            splits,
        ) as TickerBalanceCalculator.GainTax

        assertThat(actual.tax).isEqualTo(BigDecimal("2.50"))
    }

    @Test
    fun `calculate tax for one buy and half sell then buy and then sell and then half sell`() {
        val transactions = listOf(
            transaction(
                TransactionType.BUY,
                quantity = BigDecimal("1"),
                pricePerShare = BigDecimal("1"),
                totalAmount = BigDecimal("1"),
            ),
            transaction(
                TransactionType.SELL,
                quantity = BigDecimal("0.5"),
                pricePerShare = BigDecimal("2"),
                totalAmount = BigDecimal("1"),
            ),
            transaction(
                TransactionType.BUY,
                quantity = BigDecimal("1"),
                pricePerShare = BigDecimal("3"),
                totalAmount = BigDecimal("3"),
            ),
            transaction(
                TransactionType.SELL,
                quantity = BigDecimal("1"),
                pricePerShare = BigDecimal("4"),
                totalAmount = BigDecimal("4"),
            ),
            transaction(
                TransactionType.SELL,
                quantity = BigDecimal("0.5"),
                pricePerShare = BigDecimal("2"),
                totalAmount = BigDecimal("2"),
            ),
        )

        val actual = TickerBalanceCalculator(
            BigDecimal.ONE
        ).calculateResult(
            transactions,
            startDate.rangeTo(endDate),
            splits,
        ) as TickerBalanceCalculator.GainTax

        assertThat(actual.tax).isEqualTo(BigDecimal("3.00"))
    }


    @Test
    fun `calculate sample for 2019`() {
        val transactions = ReportParser().parse(File("src/test/resources/sample.csv").readText())

        val actual = TickerBalanceCalculator().calculateResult(
            transactions,
            LocalDate(2019, 1, 1).rangeTo(LocalDate(2019, 12, 31)),
            splits,
        ) as TickerBalanceCalculator.GainTax

        assertThat(actual.tax).isZero
    }

    @Test
    fun `calculate sample for 2020`() {
        val transactions = ReportParser().parse(File("src/test/resources/sample.csv").readText())

        val actual = TickerBalanceCalculator().calculateResult(
            transactions,
            LocalDate(2020, 1, 1).rangeTo(LocalDate(2020, 12, 31)),
            splits,
        ) as TickerBalanceCalculator.GainTax

        assertThat(actual.tax).isCloseTo(BigDecimal("173.29"), Offset.offset(BigDecimal("0.01")))
    }

    @Test
    fun `calculate sample for 2021`() {
        val transactions = ReportParser().parse(File("src/test/resources/sample.csv").readText())

        val actual = TickerBalanceCalculator().calculateResult(
            transactions,
            LocalDate(2021, 1, 1).rangeTo(LocalDate(2021, 12, 31)),
            splits,
        ) as TickerBalanceCalculator.Loss

        assertThat(actual.loss).isCloseTo(BigDecimal("-1105.56"), Offset.offset(BigDecimal("0.01")))
    }

    @Test
    fun `calculate sample 2 (after optimisations) for 2021`() {
        val transactions = ReportParser().parse(File("src/test/resources/sample2.csv").readText())

        val actual = TickerBalanceCalculator().calculateResult(
            transactions,
            LocalDate(2021, 1, 1).rangeTo(LocalDate(2021, 12, 31)),
            splits,
        ) as TickerBalanceCalculator.GainTax

        assertThat(actual.tax).isCloseTo(BigDecimal("30.93"), Offset.offset(BigDecimal("0.01")))
    }

    @Test
    fun `calculate properly with splits`() {
        val inputTransactions = ReportParser().parse(splitsInput)
        val splits = SplitParser().parse(
            File("src/main/resources/stockSplits.csv").readText()
        )

        val actual = TickerBalanceCalculator(taxRatePercent = BigDecimal.ONE).calculateSumOfTickerTaxes(
            allTransactions = inputTransactions,
            LocalDate(2021, 1, 1).rangeTo(LocalDate(2021, 7, 3)),
            splits
        )

        assertThat(actual)
            .isEqualTo("70.07")
    }

    @Test
    fun `calculate properly with splits 2`() {
        val inputTransactions = ReportParser().parse(splitsInput)
        val splits = SplitParser().parse(
            File("src/main/resources/stockSplits.csv").readText()
        )

        val actual = TickerBalanceCalculator(taxRatePercent = BigDecimal.ONE).calculateSumOfTickerTaxes(
            allTransactions = inputTransactions,
            LocalDate(2021, 1, 1).rangeTo(LocalDate(2021, 7, 30)),
            splits
        )

        assertThat(actual)
            .isEqualTo(BigDecimal("88.25"))
    }

    @Test
    fun `calculate properly with splits 3`() {
        val inputTransactions = ReportParser().parse(splitsInput)
        val splits = SplitParser().parse(
            File("src/main/resources/stockSplits.csv").readText()
        )

        val actual = TickerBalanceCalculator(taxRatePercent = BigDecimal.ONE).calculateSumOfTickerTaxes(
            allTransactions = inputTransactions,
            LocalDate(2021, 1, 1).rangeTo(LocalDate(2021, 11, 30)),
            splits
        )

        assertThat(actual)
            .isEqualTo(BigDecimal("626.84"))
    }

    private fun transaction(
        transactionType: TransactionType,
        quantity: BigDecimal,
        pricePerShare: BigDecimal,
        totalAmount: BigDecimal,
        ticker: String = "TCK",
    ) =
        Transaction(startDate,
            ticker,
            transactionType,
            quantity,
            pricePerShare,
            totalAmount,
            "PLN"
        )
}