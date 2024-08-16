package com.hocheol.humandetectapp.ui.viewmodel

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hocheol.humandetectapp.model.DelegateType
import com.hocheol.humandetectapp.model.DetectionConfig
import com.hocheol.humandetectapp.model.ModelType
import com.hocheol.humandetectapp.ui.components.OverlayView
import com.hocheol.humandetectapp.utils.ObjectDetectorHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.detector.Detection
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val objectDetectorHelper: ObjectDetectorHelper
) : ViewModel() {

    var inferenceTime by mutableLongStateOf(0L)
        private set

    var detectionConfig by mutableStateOf(DetectionConfig())
        private set

    var previewView: PreviewView? by mutableStateOf(null)
    var overlayView: OverlayView? by mutableStateOf(null)

    init {
        initObjectDetectorListener()
        initDetections()
    }

    private fun initObjectDetectorListener() {
        objectDetectorHelper.setObjectDetectorListener(object : ObjectDetectorHelper.DetectorListener {
            override fun onError(error: String) {
                Log.e("ObjectDetector", "Object detection error: $error")
            }

            override fun onResults(
                results: MutableList<Detection>?,
                inferenceTime: Long,
                imageHeight: Int,
                imageWidth: Int
            ) {
                this@CameraViewModel.inferenceTime = inferenceTime
                overlayView?.run {
                    setResults(results ?: mutableListOf(), imageHeight, imageWidth)
                    invalidate()
                }
            }
        })
    }

    private fun initDetections() {
        objectDetectorHelper.clearObjectDetector()
        objectDetectorHelper.setConfig(detectionConfig)
        objectDetectorHelper.setupObjectDetector()
        overlayView?.clear()
    }

    fun updateThreshold(newThreshold: Float) {
        detectionConfig = detectionConfig.copy(threshold = newThreshold)
        initDetections()
    }

    fun updateMaxResults(newMaxResults: Int) {
        detectionConfig = detectionConfig.copy(maxResults = newMaxResults)
        initDetections()
    }

    fun updateNumThreads(newNumThreads: Int) {
        detectionConfig = detectionConfig.copy(numThreads = newNumThreads)
        initDetections()
    }

    fun updateDelegate(newDelegate: Int) {
        detectionConfig = detectionConfig.copy(delegate = DelegateType.entries[newDelegate])
        initDetections()
    }

    fun updateModel(newModel: Int) {
        detectionConfig = detectionConfig.copy(model = ModelType.entries[newModel])
        initDetections()
    }

    fun analyzeImage(imageProxy: ImageProxy) {
        viewModelScope.launch(Dispatchers.Default) {
            val imageBitmap = imageProxy.toBitmap()
            objectDetectorHelper.detect(imageBitmap, imageProxy.imageInfo.rotationDegrees)
            imageProxy.close()
        }
    }
}
