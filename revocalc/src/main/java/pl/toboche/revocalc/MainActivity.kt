package pl.toboche.revocalc

import CapitalGainCalculator
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RevolutTradingIncomeCalcTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var filePath by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { reportUri ->
            if (reportUri != null) {
                filePath = reportUri
            }
        }
    val coroutineScope = rememberCoroutineScope()
    var result by remember { mutableStateOf<CapitalGainCalculator.GainAndExpenses?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (filePath == null) {
            Text(text = stringResource(id = R.string.no_report_selected))
            Spacer(Modifier.size(16.dp))
            Button(onClick = {
                launcher.launch("text/csv")
            }) {
                Text(text = stringResource(id = R.string.select_report))
            }
        } else {
            val current = LocalContext.current
            Button(onClick = {
                coroutineScope.launch {
                    val inputStream = current.contentResolver.openInputStream(filePath!!)!!
                    withContext(Dispatchers.IO) {
                        val content = inputStream.bufferedReader().use(BufferedReader::readText)
                        result = CapitalGainCalculator().calculate(
                            content,
                            LocalDate(2021, 1, 1),
                            LocalDate(2021, 12, 31),
                            ""
                        )
                    }
                }
            }) {
                Text(stringResource(R.string.compute))
            }
            if (result != null) {
                GainAndExpenses(result!!)
            }
        }
    }
}

@Composable
fun GainAndExpenses(gainAndExpenses: CapitalGainCalculator.GainAndExpenses) {
    Column {
        GainAndExpensesHeader("PIT-38 - rozliczenia zysków z akcji")
        GainAndExpensesHeader("Sekcja „Dochody / straty”")
        ResultItem(
            stringResource(R.string.capital_income),
            gainAndExpenses.income
        )
        ResultItem(
            stringResource(R.string.costs),
            gainAndExpenses.cost
        )
        GainAndExpensesHeader("PIT-36/PIT-36L/PIT-38 - rozliczenia dywidend")
        GainAndExpensesHeader("Sekcja „Kwota do zapłaty / nadpłata”")
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
            gainAndExpenses.dividendTaxLeftToPay
        )
        GainAndExpensesHeader(text = "Dodatkowe informacje")
        ResultItem(
            stringResource(R.string.custody_fees_header),
            gainAndExpenses.custodyFees
        )
        ResultItem(
            stringResource(R.string.tax_to_pay),
            gainAndExpenses.finallyToPay
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
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(title)
        Text(
            modifier = Modifier.weight(2f, fill = false),
            text = NumberFormat.getCurrencyInstance().format(value),
            style = style ?: MaterialTheme.typography.h6
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RevolutTradingIncomeCalcTheme {
        MainScreen()
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
            income = "2312".toBigDecimal(),
            cost = "999.9999".toBigDecimal()
        )
    )
}