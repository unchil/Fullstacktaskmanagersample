package com.unchil.full_stack_task_manager_sample

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    CompositionLocalProvider( LocalPlatform provides getPlatform() ) {
        OceanWaterInfo()
    }
}