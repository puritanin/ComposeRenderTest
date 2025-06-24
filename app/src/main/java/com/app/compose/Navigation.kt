package com.app.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach

@Composable
internal fun Navigation(
    onClick: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Screen.entries.fastForEach { screen ->
            Button(text = screen.name, onClick = { onClick(screen) })
        }
    }
}

@Composable
private fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        border = BorderStroke(width = 1.dp, Color.Black),
        modifier = modifier,
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            modifier = modifier.padding(4.dp),
        )
    }
}

enum class Screen {
    HEADER,
    CIRCLE_BLUR,
    CIRCLE_MAGNIFIER,
}
