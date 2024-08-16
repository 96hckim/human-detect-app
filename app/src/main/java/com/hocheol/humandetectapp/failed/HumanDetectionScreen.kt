package com.hocheol.humandetectapp.failed

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun HumanDetectionScreen() {
    var detections by remember { mutableStateOf(emptyList<Detection>()) }
    val context = LocalContext.current
    val model = remember { HumanDetectionModel(context) }

    DisposableEffect(Unit) {
        onDispose {
            model.close() // Clean up resources when Composable leaves the composition
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(modifier = Modifier.fillMaxSize()) { imageProxy ->
            val bitmap = imageProxy.toBitmap()
            val inputBuffer = model.preprocessImage(bitmap, 320)  // Assuming model input size 320x320
            detections = model.runInference(inputBuffer)
            imageProxy.close()
        }

        DetectionsOverlay(detections)
    }
}

@Composable
fun DetectionsOverlay(detections: List<Detection>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val paint = Paint().apply {
            color = Color.Red.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = 8.dp.toPx()
        }

        detections.forEach { detection ->
            drawRect(
                color = Color.Red,
                topLeft = Offset(detection.location.left, detection.location.top),
                size = Size(
                    detection.location.width(),
                    detection.location.height()
                ),
                style = Stroke(width = 4.dp.toPx())
            )

            drawContext.canvas.nativeCanvas.drawText(
                "${detection.title} ${detection.confidence}",
                detection.location.left,
                detection.location.top,
                paint
            )
        }
    }
}
