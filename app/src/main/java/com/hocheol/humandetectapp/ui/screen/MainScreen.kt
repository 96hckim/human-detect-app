package com.hocheol.humandetectapp.ui.screen

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hocheol.humandetectapp.R
import com.hocheol.humandetectapp.ui.components.DetectionSettingsSheet
import com.hocheol.humandetectapp.ui.navigation.MainRoute
import com.hocheol.humandetectapp.ui.viewmodel.CameraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val cameraViewModel: CameraViewModel = hiltViewModel()
    val layoutDirection = LocalLayoutDirection.current

    val scaffoldState = rememberBottomSheetScaffoldState()

    Surface {
        BottomSheetScaffold(
            sheetContent = {
                DetectionSettingsSheet(cameraViewModel)
            },
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = MainRoute.PERMISSION.route,
                modifier = Modifier.padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection)
                )
            ) {
                composable(route = MainRoute.PERMISSION.route) {
                    PermissionScreen(navController = navController)
                }

                composable(route = MainRoute.CAMERA.route) {
                    CameraScreen(cameraViewModel) // HumanDetectionScreen()
                }
            }
        }
    }
}