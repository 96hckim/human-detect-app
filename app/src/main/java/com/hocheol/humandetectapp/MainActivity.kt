package com.hocheol.humandetectapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hocheol.humandetectapp.ui.screen.MainScreen
import com.hocheol.humandetectapp.ui.theme.HumanDetectAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HumanDetectAppTheme {
                MainScreen()
            }
        }
    }
}