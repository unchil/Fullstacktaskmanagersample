package com.unchil.full_stack_task_manager_sample

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val alias: PlatformAlias
        get() = PlatformAlias.ANDROID
}

actual fun getPlatform(): Platform = AndroidPlatform()