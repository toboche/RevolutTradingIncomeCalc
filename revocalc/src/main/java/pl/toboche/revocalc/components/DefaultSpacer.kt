package pl.toboche.revocalc.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pl.toboche.revocalc.spacerSize

@Composable
internal fun DefaultSpacer() {
    Spacer(Modifier.size(spacerSize))
}