package pl.toboche.revocalc

import CapitalGainCalculator
import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import pl.toboche.revocalc.data.GainAndExpensesResult
import java.io.BufferedReader
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    var reportUri: Uri? = null

    val showReportPathChoosing: Boolean get() = reportUri == null

    var loading by mutableStateOf(false)

    var result by mutableStateOf<GainAndExpensesResult?>(null)

    var errorLoading by mutableStateOf(false)

    fun loadResults() {
        viewModelScope.launch {
            loading = true
            try {
                val inputStream =
                    getApplication<Application>().contentResolver.openInputStream(reportUri!!)!!
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
    }
}