package com.unchil.full_stack_task_manager_sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Fullstacktaskmanagersample",
    ) {
        App()
    }
}