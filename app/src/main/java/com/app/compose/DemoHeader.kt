package com.app.compose

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun DemoHeader(
    modifier: Modifier = Modifier,
) {
    val state = rememberGraphicsLayerRecordingState()

    Box(modifier = modifier.testTag("DemoHeader")) {
        Content(modifier = Modifier.recordLayer(state = state))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter)
                .renderLayerWithBlurEffect(state = state)
                .background(color = Color.Gray.copy(alpha = 0.2f)),
        )
    }
}

@Composable
private fun Modifier.renderLayerWithBlurEffect(state: GraphicsLayerRecordingState): Modifier {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return this

    val renderEffect = remember { RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP) }

    return this
        .onGloballyPositioned { layoutCoordinates -> state.setRectRegion(rect = layoutCoordinates.boundsInParent()) }
        .renderLayer(state = state, renderEffect = renderEffect)
}
