package pl.toboche.revocalc.components

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import java.math.BigDecimal
import java.text.NumberFormat

@Composable
internal fun ResultItem(title: String, value: BigDecimal, style: TextStyle? = null) {
    Text(
        title,
        style = style ?: MaterialTheme.typography.body2
    )
    Text(
        modifier = Modifier
            .wrapContentHeight(unbounded = true),
        text = NumberFormat.getCurrencyInstance().format(value),
        style = style ?: MaterialTheme.typography.body1
    )
    DefaultSpacer()
}