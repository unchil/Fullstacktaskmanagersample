package com.unchil.full_stack_task_manager_sample

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {

    val state = WindowState(
        size = DpSize(1400.dp, 800.dp),
        position = WindowPosition(Alignment.TopCenter)
    )


    Window(
        onCloseRequest = ::exitApplication,
        title = "Fullstacktaskmanagersample",
        state = state,
    ) {
        CompositionLocalProvider( LocalPlatform provides getPlatform() ) {
            OceanWaterInfo()
        }
    }
}