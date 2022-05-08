package pl.toboche.revocalc

import CapitalGainCalculator
import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
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

    val reportUri: MutableLiveData<Uri?> = MutableLiveData(null)

    val showReportPathChoosing: LiveData<Boolean> = Transformations.map(reportUri) {
        it == null
    }

    var loading by mutableStateOf(false)
        private set

    var result by mutableStateOf<GainAndExpensesResult?>(null)
        private set

    var errorLoading by mutableStateOf(false)
        private set

    fun loadResults() {
        loading = true
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val inputStream =
                        getApplication<Application>().contentResolver.openInputStream(reportUri.value!!)!!
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