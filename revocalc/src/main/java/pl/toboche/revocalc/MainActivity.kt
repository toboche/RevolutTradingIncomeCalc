package pl.toboche.revocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.unit.dp
import pl.toboche.revocalc.components.MainScreen
import pl.toboche.revocalc.ui.theme.RevolutTradingIncomeCalcTheme

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

internal val spacerSize = 16.dp