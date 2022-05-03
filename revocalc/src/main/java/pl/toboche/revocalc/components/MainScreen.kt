package pl.toboche.revocalc.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.toboche.revocalc.MainScreenViewModel
import pl.toboche.revocalc.R
import pl.toboche.revocalc.spacerSize
import pl.toboche.revocalc.ui.theme.RevolutTradingIncomeCalcTheme

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel()
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { reportUri ->
            viewModel.reportUri = reportUri
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacerSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (viewModel.showReportPathChoosing) {
            NoReportSelected(launcher)
        } else {
            Button(
                enabled = !viewModel.loading,
                onClick = { viewModel.loadResults() }) {
                Text(stringResource(R.string.compute))
            }
            DefaultSpacer()
            if (viewModel.errorLoading) {
                Text(text = "Problem z obliczaniem, spróbuj ponownie lub skontaktuj się z nami.")
                DefaultSpacer()
            }
            if (viewModel.loading) {
                CircularProgressIndicator()
                DefaultSpacer()
            }
            if (viewModel.result != null) {
                GainAndExpenses(viewModel.result!!)
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
        MainScreen()
    }
}
