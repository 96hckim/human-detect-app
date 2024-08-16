package com.hocheol.humandetectapp.failed

import android.graphics.RectF

data class Detection(
    val id: String,
    val title: String,
    val confidence: Float,
    val location: RectF
)