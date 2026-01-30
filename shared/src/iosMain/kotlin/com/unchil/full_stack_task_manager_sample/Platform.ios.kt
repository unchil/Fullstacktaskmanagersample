package com.unchil.full_stack_task_manager_sample

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    override val alias: PlatformAlias
        get() = PlatformAlias.IOS
}

actual fun getPlatform(): Platform = IOSPlatform()