package pl.toboche.revocalc

import CapitalGainCalculator
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import pl.toboche.revocalc.ui.theme.RevolutTradingIncomeCalcTheme
import java.io.BufferedReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val passedUri = intent.clipData?.getItemAt(0)?.uri ?: intent.data
        setContent {
            RevolutTradingIncomeCalcTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(passedUri)
                }
            }
        }
    }
}

private val spacerSize = 16.dp

@Composable
fun MainScreen(passedUri: Uri?) {
    var filePath by remember { mutableStateOf<Uri?>(passedUri) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { reportUri ->
            if (reportUri != null) {
                filePath = reportUri
            }
        }
    val coroutineScope = rememberCoroutineScope()
    var result by remember { mutableStateOf<CapitalGainCalculator.GainAndExpenses?>(null) }
    var loading by remember { mutableStateOf(false) }
    var errorLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacerSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (filePath == null) {
            Text(text = stringResource(id = R.string.no_report_selected))
            DefaultSpacer()
            Button(onClick = {
                launcher.launch("text/csv")
            }) {
                Text(text = stringResource(id = R.string.select_report))
            }
        } else {
            val current = LocalContext.current
            Button(
                enabled = !loading,
                onClick = {
                    coroutineScope.launch {
                        loading = true
                        try {
                            val inputStream = current.contentResolver.openInputStream(filePath!!)!!
                            withContext(Dispatchers.IO) {
                                val content =
                                    inputStream.bufferedReader().use(BufferedReader::readText)
                                result = CapitalGainCalculator().calculate(
                                    content,
                                    LocalDate(2021, 1, 1),
                                    LocalDate(2021, 12, 31),
                                    ""
                                )
                                errorLoading = false
                            }
                        } catch (exception: Exception) {
                            errorLoading = true
                        }
                        loading = false
                    }
                }) {
                Text(stringResource(R.string.compute))
            }
            DefaultSpacer()
            if (errorLoading) {
                Text(text = "Problem z obliczaniem, spróbuj ponownie lub skontaktuj się z nami.")
                DefaultSpacer()
            }
            if (loading) {
                CircularProgressIndicator()
                DefaultSpacer()
            }
            if (result != null) {
                GainAndExpenses(result!!)
            }
        }
    }
}

@Composable
fun GainAndExpenses(gainAndExpenses: CapitalGainCalculator.GainAndExpenses) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        DefaultSpacer()

        GainAndExpensesHeader("PIT-38 - rozliczenia zysków z akcji")
        GainAndExpensesHeader("Sekcja „Dochody / straty”")
        DefaultSpacer()

        ResultItem(
            stringResource(R.string.capital_income),
            gainAndExpenses.tradingIncome
        )
        ResultItem(
            stringResource(R.string.costs),
            gainAndExpenses.tradingCost
        )

        DefaultSpacer()
        Divider()
        DefaultSpacer()
        GainAndExpensesHeader("PIT-36/PIT-36L/PIT-38 - rozliczenia dywidend")
        GainAndExpensesHeader("Sekcja „Kwota do zapłaty / nadpłata”")
        DefaultSpacer()

        ResultItem(
            stringResource(R.string.dividend_tax_already_paid_header),
            gainAndExpenses.dividendTaxAlreadyPaid
        )
        ResultItem(
            stringResource(R.string.dividend_total_dividend_tax_to_pay_header),
            gainAndExpenses.totalDividendTaxToPay
        )
        ResultItem(
            stringResource(R.string.dividend_tax_header),
            gainAndExpenses.dividendTaxLeftToPay.setScale(0, RoundingMode.HALF_UP)
        )

        DefaultSpacer()
        Divider()
        DefaultSpacer()
        GainAndExpensesHeader(text = "Dodatkowe informacje")
        DefaultSpacer()

        ResultItem(
            stringResource(R.string.custody_fees_header),
            gainAndExpenses.custodyFees
        )
        ResultItem(
            stringResource(R.string.tax_to_pay),
            gainAndExpenses.finallyToPay
        )
        ResultItem(
            stringResource(R.string.total_gain),
            gainAndExpenses.totalGrossIncome
        )
    }
}

@Composable
private fun GainAndExpensesHeader(text: String) {
    Text(
        text = text,
        Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h5
    )
}

@Composable
private fun ResultItem(title: String, value: BigDecimal, style: TextStyle? = null) {
    Text(
        title,
        style = style ?: MaterialTheme.typography.body2
    )
    Text(
        modifier = Modifier
            .wrapContentHeight(unbounded = true),
        text = NumberFormat.getCurrencyInstance().format(value),
        style = style ?: MaterialTheme.typography.body1
    )
    DefaultSpacer()
}

@Composable
private fun DefaultSpacer() {
    Spacer(Modifier.size(spacerSize))
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RevolutTradingIncomeCalcTheme {
        MainScreen(null)
    }
}

@Preview(showBackground = true)
@Composable
fun GainAndExpensesViewPreview() {
    GainAndExpenses(
        CapitalGainCalculator.GainAndExpenses(
            dividendTaxLeftToPay = BigDecimal("123.33"),
            custodyFees = BigDecimal("0.33"),
            tickerGainCalculationResult = BigDecimal("44234.33"),
            finallyToPay = BigDecimal("11111.22"),
            finalLoss = BigDecimal.ZERO,
            dividendTaxAlreadyPaid = "0.1".toBigDecimal(),
            totalDividendTaxToPay = "111.4434".toBigDecimal(),
            tradingIncome = "2312".toBigDecimal(),
            tradingCost = "999.9999".toBigDecimal(),
            totalGrossIncome = "999.9999".toBigDecimal()
        )
    )
}