package com.hocheol.humandetectapp.model

data class DetectionConfig(
    var threshold: Float = 0.5f,
    var maxResults: Int = 4,
    var numThreads: Int = 3,
    var delegate: DelegateType = DelegateType.CPU,
    var model: ModelType = ModelType.EFFICIENTDET_V2
)

enum class DelegateType(val displayName: String) {
    CPU("CPU"),
    GPU("GPU"),
    NNAPI("NNAPI");
}

enum class ModelType(val displayName: String) {
    MOBILENET_V1("mobilenetv1"),
    EFFICIENTDET_V0("efficientdet-lite0"),
    EFFICIENTDET_V1("efficientdet-lite1"),
    EFFICIENTDET_V2("efficientdet-lite2"),
    NP_CONVERTED_PEOPLE_DETECTION("NP-converted-people_detection");

    fun getFileName(): String = "$displayName.tflite"
}