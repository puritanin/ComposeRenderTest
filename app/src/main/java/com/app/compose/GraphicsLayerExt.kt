package com.app.compose

import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.CanvasHolder
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntSize

fun Modifier.recordLayer(
    state: GraphicsLayerRecordingState,
): Modifier {
    return this.drawWithContent {
        drawContent()

        when (val region = state.region.value) {
            is GraphicsLayerRecordingState.Region.Rectangle -> {
                recordRectangleLayer(graphicsLayer = state.graphicsLayer, rect = region.rect)
            }
            is GraphicsLayerRecordingState.Region.Circle -> {
                recordCircleLayer(graphicsLayer = state.graphicsLayer, center = region.center, size = region.size)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun Modifier.renderLayer(
    state: GraphicsLayerRecordingState,
    renderEffect: RenderEffect,
): Modifier {
    return this.renderLayer(graphicsLayer = state.graphicsLayer, renderEffect = renderEffect)
}

private fun ContentDrawScope.recordRectangleLayer(
    graphicsLayer: GraphicsLayer,
    rect: Rect,
) {
    graphicsLayer.record(size = IntSize(rect.width.toInt(), rect.height.toInt())) {
        translate(left = -rect.left, top = -rect.top) {
            clipRect(
                left = rect.left,
                top = rect.top,
                right = rect.right,
                bottom = rect.bottom,
                clipOp = ClipOp.Intersect,
            ) {
                this@recordRectangleLayer.drawContent()
            }
        }
    }
}

private fun ContentDrawScope.recordCircleLayer(
    graphicsLayer: GraphicsLayer,
    center: Offset,
    size: Size,
) {
    val rect = Rect(offset = center, size = size)
    val path = Path().apply { addOval(rect) }

    graphicsLayer.record(size = size.toIntSize()) {
        translate(left = -rect.left, top = -rect.top) {
            clipPath(path = path, clipOp = ClipOp.Intersect) {
                this@recordCircleLayer.drawContent()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
private fun Modifier.renderLayer(
    graphicsLayer: GraphicsLayer,
    renderEffect: RenderEffect,
): Modifier {
    val renderNode = RenderNode("RenderNode")
    val canvasHolder = CanvasHolder()

    return this.drawBehind {
        renderNode.setRenderEffect(renderEffect)
        renderNode.setPosition(0, 0, size.width.toInt(), size.height.toInt())

        drawIntoCanvas { canvas ->
            val recordingCanvas = renderNode.beginRecording()
            canvasHolder.drawInto(recordingCanvas) {
                drawContext.canvas = this@drawInto
                drawLayer(graphicsLayer)
                drawContext.canvas = canvas
            }
            renderNode.endRecording()
            canvas.nativeCanvas.drawRenderNode(renderNode)
        }
    }
}

@Composable
fun rememberGraphicsLayerRecordingState(
    regionState: MutableState<GraphicsLayerRecordingState.Region> = rememberGraphicsLayerRecordingRegion(),
): GraphicsLayerRecordingState {
    val graphicsLayer = rememberGraphicsLayer()
    return remember { GraphicsLayerRecordingState(graphicsLayer = graphicsLayer, region = regionState) }
}

@Composable
fun rememberGraphicsLayerRecordingRegion(): MutableState<GraphicsLayerRecordingState.Region> {
    return remember { mutableStateOf(GraphicsLayerRecordingState.Region.Rectangle(rect = Rect.Zero)) }
}

@Stable
data class GraphicsLayerRecordingState(
    val graphicsLayer: GraphicsLayer,
    val region: MutableState<Region>,
) {
    sealed class Region {
        data class Rectangle(val rect: Rect) : Region()
        data class Circle(val center: Offset, val size: Size) : Region()
    }

    fun setRectRegion(rect: Rect) {
        this.region.value = Region.Rectangle(rect = rect)
    }

    fun setCircleRegion(center: Offset, size: Size) {
        this.region.value = Region.Circle(center = center, size = size)
    }
}
