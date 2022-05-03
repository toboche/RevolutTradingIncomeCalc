package pl.toboche.revocalc.components

import CapitalGainCalculator
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import pl.toboche.revocalc.MainScreenViewModel
import pl.toboche.revocalc.R
import pl.toboche.revocalc.data.GainAndExpensesResult
import pl.toboche.revocalc.spacerSize
import pl.toboche.revocalc.ui.theme.RevolutTradingIncomeCalcTheme
import java.io.BufferedReader

@Composable
fun MainScreen(
    passedUri: Uri?,
    intent: Intent,
    viewModel: MainScreenViewModel = viewModel()
) {
    var filePath by rememberSaveable { mutableStateOf(passedUri) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { reportUri ->
            if (reportUri != null) {
                filePath = reportUri
            }
        }
    val coroutineScope = rememberCoroutineScope()
    var result by rememberSaveable { mutableStateOf<GainAndExpensesResult?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }
    var errorLoading by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacerSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (viewModel.showReportPathChoosing) {
            NoReportSelected(launcher)
        } else {
            val current = LocalContext.current
            Button(
                enabled = !loading,
                onClick = {
                    coroutineScope.launch {
                        loading = true
                        try {
                            val inputStream =
                                current.contentResolver.openInputStream(filePath!!)!!
                            withContext(Dispatchers.IO) {
                                val content =
                                    inputStream.bufferedReader().use(BufferedReader::readText)
                                result = GainAndExpensesResult(
                                    CapitalGainCalculator().calculate(
                                        content,
                                        LocalDate(2021, 1, 1),
                                        LocalDate(2021, 12, 31),
                                        ""
                                    )
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
private fun NoReportSelected(launcher: ManagedActivityResultLauncher<String, Uri?>) {
    Text(text = stringResource(id = R.string.no_report_selected))
    DefaultSpacer()
    Button(onClick = {
        launcher.launch("text/csv")
    }) {
        Text(text = stringResource(id = R.string.select_report))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RevolutTradingIncomeCalcTheme {
        MainScreen(null, Intent())
    }
}
