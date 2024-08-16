package com.hocheol.humandetectapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.hocheol.humandetectapp.model.DelegateType
import com.hocheol.humandetectapp.model.DetectionConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObjectDetectorHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private var objectDetector: ObjectDetector? = null
    private var objectDetectorListener: DetectorListener? = null
    private var detectionConfig: DetectionConfig = DetectionConfig()

    fun clearObjectDetector() {
        objectDetector = null
    }

    fun setObjectDetectorListener(detectorListener: DetectorListener) {
        objectDetectorListener = detectorListener
    }

    fun setConfig(config: DetectionConfig) {
        detectionConfig = config
    }

    // Initialize the object detector using settings from DetectionConfig
    fun setupObjectDetector() {
        // Create the base options for the detector using specifies max results and score threshold
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(detectionConfig.threshold)
            .setMaxResults(detectionConfig.maxResults)

        // Set general detection options, including number of used threads
        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(detectionConfig.numThreads)

        // Use the specified hardware for running the model. Default to CPU
        when (detectionConfig.delegate) {
            DelegateType.CPU -> {
                // Default
            }

            DelegateType.GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {
                    objectDetectorListener?.onError("GPU is not supported on this device")
                }
            }

            DelegateType.NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(context, detectionConfig.model.getFileName(), optionsBuilder.build())
        } catch (e: IllegalStateException) {
            objectDetectorListener?.onError(
                "Object detector failed to initialize. See error logs for details"
            )
            Log.e("ObjectDetectorHelper", "TFLite failed to load model with error: " + e.message)
        }
    }

    fun detect(image: Bitmap, imageRotation: Int) {
        if (objectDetector == null) {
            setupObjectDetector()
        }

        // Inference time is the difference between the system time at the start and finish of the process
        var inferenceTime = SystemClock.uptimeMillis()

        // Create preprocessor for the image.
        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-imageRotation / 90))
            .build()

        // Preprocess the image and convert it into a TensorImage for detection.
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        objectDetectorListener?.onResults(
            results,
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }
}
