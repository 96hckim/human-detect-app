package com.hocheol.humandetectapp.failed

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class HumanDetectionModel(context: Context) {

    private var interpreter: Interpreter? = null

    init {
        interpreter = Interpreter(loadModelFile(context))
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd("NP-converted-people_detection.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }

    fun preprocessImage(bitmap: Bitmap, inputSize: Int): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        resizedBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixelValue in intValues) {
            byteBuffer.putFloat(((pixelValue shr 16 and 0xFF) / 255.0f))
            byteBuffer.putFloat(((pixelValue shr 8 and 0xFF) / 255.0f))
            byteBuffer.putFloat(((pixelValue and 0xFF) / 255.0f))
        }

        return byteBuffer
    }

    fun runInference(byteBuffer: ByteBuffer): List<Detection> {
        val output = Array(1) { Array(3) { Array(20) { Array(20) { FloatArray(6) } } } }
        interpreter?.run(byteBuffer, output)

        val detections = mutableListOf<Detection>()

        for (i in 0 until 3) {
            for (y in 0 until 20) {
                for (x in 0 until 20) {
                    val data = output[0][i][y][x]
                    val xCenter = data[0]
                    val yCenter = data[1]
                    val width = data[2]
                    val height = data[3]
                    val confidence = data[4]
                    val classIndex = data[5].toInt()

                    if (confidence > 0.5) {
                        val rectF = RectF(
                            xCenter - width / 2,
                            yCenter - height / 2,
                            xCenter + width / 2,
                            yCenter + height / 2
                        )
                        detections.add(Detection(classIndex.toString(), "Person", confidence, rectF))
                    }
                }
            }
        }

        Log.d("InferenceDebug", "Detections count: ${detections.size}")
        return detections
    }
}
