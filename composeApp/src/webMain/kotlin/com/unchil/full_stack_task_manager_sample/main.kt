package com.unchil.full_stack_task_manager_sample

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        CompositionLocalProvider( LocalPlatform provides getPlatform() ) {
            DataGridWithViewModel()
        }
    }
}