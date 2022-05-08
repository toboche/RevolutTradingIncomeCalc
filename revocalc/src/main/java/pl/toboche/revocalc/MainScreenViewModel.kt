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
    application: Application,
    private val state: SavedStateHandle
) : AndroidViewModel(application) {

    private val _reportUri: MutableLiveData<Uri?> = state.getLiveData("reportUri", null)

    val showReportPathChoosing: LiveData<Boolean> = Transformations.map(_reportUri) {
        it == null
    }

    var loading by mutableStateOf(false)
        private set

    var result = MutableLiveData<GainAndExpensesResult?>(null)

    var errorLoading by mutableStateOf(false)
        private set

    fun setReportUri(uri: Uri?) {
        if (uri == null) return
        _reportUri.value = uri
        state["reportUri"] = uri
    }

    fun loadResults() {
        loading = true
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    @Suppress("BlockingMethodInNonBlockingContext") val inputStream =
                        getApplication<Application>().contentResolver.openInputStream(_reportUri.value!!)!!
                    val content =
                        inputStream.bufferedReader().use(BufferedReader::readText)
                    result.postValue(
                        GainAndExpensesResult(
                            CapitalGainCalculator().calculate(
                                content,
                                LocalDate(2021, 1, 1),
                                LocalDate(2021, 12, 31),
                                ""
                            )
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