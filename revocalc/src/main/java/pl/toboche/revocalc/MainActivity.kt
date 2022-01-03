package pl.toboche.revocalc

import CapitalGainCalculator
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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

    if (filePath == null) {
        Column {
            Text(text = stringResource(id = R.string.no_report_selected))
            Spacer(Modifier.size(16.dp))
            Button(onClick = {
                launcher.launch("text/csv")
            }) {
                Text(text = stringResource(id = R.string.select_report))
            }
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
            GainAndExpensesView(result!!)
        }
    }
}

@Composable
fun GainAndExpensesView(gainAndExpenses: CapitalGainCalculator.GainAndExpenses) {
    Column {
        ResultItem(
            stringResource(R.string.dividend_tax_header),
            gainAndExpenses.dividendTaxLeftToPay
        )
        ResultItem(
            stringResource(R.string.custody_fees_header),
            gainAndExpenses.custodyFees
        )
        ResultItem(
            stringResource(R.string.capital_gain),
            gainAndExpenses.tickerGainCalculationResult
        )
        ResultItem(
            stringResource(R.string.loss),
            gainAndExpenses.finalLoss
        )
        ResultItem(
            style = MaterialTheme.typography.h4,
            title = stringResource(R.string.tax_to_pay),
            value = gainAndExpenses.finallyToPay
        )
    }
}

@Composable
private fun ResultItem(title: String, value: BigDecimal, style: TextStyle? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = style ?: LocalTextStyle.current)
        Text(
            text = NumberFormat.getCurrencyInstance().format(value),
            style = style ?: MaterialTheme.typography.h5
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
    GainAndExpensesView(
        CapitalGainCalculator.GainAndExpenses(
            dividendTaxLeftToPay = BigDecimal("123.33"),
            custodyFees = BigDecimal("0.33"),
            tickerGainCalculationResult = BigDecimal("44234.33"),
            finallyToPay = BigDecimal("11111.22"),
            finalLoss = BigDecimal.ZERO
        )
    )
}