package pl.toboche.revocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import pl.toboche.revocalc.components.MainScreen
import pl.toboche.revocalc.ui.theme.RevolutTradingIncomeCalcTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val passedUri = intent.clipData?.getItemAt(0)?.uri ?: intent.data
        val viewModel: MainScreenViewModel by viewModels()
        viewModel.reportUri.value = passedUri
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

internal val spacerSize = 16.dp