package pl.toboche.revocalc

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
) : ViewModel() {
    var reportUri: Uri? = null

    val showReportPathChoosing: Boolean get() = reportUri == null

    init {
    }
}