package com.app.compose

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.intellij.lang.annotations.Language

enum class Effect {
    BLUR,
    MAGNIFIER,
}

@Composable
fun DemoCircle(
    effect: Effect,
    modifier: Modifier = Modifier,
) {
    val state = rememberGraphicsLayerRecordingState()
    var pointerOffset = remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .testTag("DemoCircle")
            .pointerInput("dragging") { detectDragGestures { _, dragAmount -> pointerOffset.value += dragAmount } }
            .onSizeChanged { pointerOffset.value = Offset(it.width / 3f, it.height / 3f) },
    ) {
        Content(modifier = Modifier.recordLayer(state = state), userScrollEnabled = false)

        Box(
            modifier = Modifier
                .size(200.dp)
                .offset { pointerOffset.value.toIntOffset() }
                .renderLayerWithEffect(state = state, centerOffset = pointerOffset, effect = effect)
                .background(color = Color.White.copy(alpha = 0.2f), shape = CircleShape),
        )
    }
}

@Composable
private fun Modifier.renderLayerWithEffect(
    state: GraphicsLayerRecordingState,
    centerOffset: MutableState<Offset>,
    effect: Effect,
): Modifier {
    return this.then(
        when (effect) {
            Effect.BLUR -> {
                Modifier.renderLayerWithBlurEffect(state = state, centerOffset = centerOffset)
            }
            Effect.MAGNIFIER -> {
                Modifier.renderLayerWithMagnifierEffect(state = state, centerOffset = centerOffset)
            }
        }
    )
}

private fun Offset.toIntOffset() = IntOffset(x = this.x.toInt(), y = this.y.toInt())

@Composable
private fun Modifier.renderLayerWithBlurEffect(
    state: GraphicsLayerRecordingState,
    centerOffset: MutableState<Offset>,
): Modifier {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return this

    val renderEffect = remember { RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP) }
    val ownSize = remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(Unit) {
        snapshotFlow { centerOffset.value to ownSize.value }
            .onEach { (center, intSize) -> state.setCircleRegion(center = center, size = intSize.toSize()) }
            .launchIn(this)
    }

    return this
        .onSizeChanged { size -> ownSize.value = size }
        .renderLayer(state = state, renderEffect = renderEffect)
}

@Composable
private fun Modifier.renderLayerWithMagnifierEffect(
    state: GraphicsLayerRecordingState,
    centerOffset: MutableState<Offset>,
): Modifier {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return this

    val shaderInput = "image"
    val shader = remember { RuntimeShader(MAGNIFIER_SHADER) }
    val renderEffect = remember { mutableStateOf(RenderEffect.createRuntimeShaderEffect(shader, shaderInput)) }
    val ownSize = remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(Unit) {
        snapshotFlow { centerOffset.value to ownSize.value }
            .onEach { (center, intSize) -> state.setCircleRegion(center = center, size = intSize.toSize()) }
            .launchIn(this)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { ownSize.value }
            .onEach { intSize ->
                val size = intSize.toSize()
                shader.setFloatUniform("size", size.width, size.height)
                renderEffect.value = RenderEffect.createRuntimeShaderEffect(shader, shaderInput)
            }
            .launchIn(this)
    }

    return this
        .onSizeChanged { size -> ownSize.value = size }
        .renderLayer(state = state, renderEffect = renderEffect.value)
}

@Language("AGSL")
private val MAGNIFIER_SHADER = """
    uniform shader image;
    uniform float2 size;
    
    half4 main(float2 fragCoord) {
        float zoomPower = 1.4;
        float radius = 0.5;
        
        float2 centerUV = float2(0.5, 0.5);
        float2 uv = fragCoord / size;
        
        float dist = distance(uv, centerUV);
        
        if (dist < radius) {
            float2 zoomedUV = centerUV + (uv - centerUV) / zoomPower;
            return image.eval(zoomedUV * size);
        }
        
        return image.eval(fragCoord);
    }
    """.trimIndent()
