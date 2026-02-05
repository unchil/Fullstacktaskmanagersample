package com.unchil.full_stack_task_manager_sample

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"

    override val alias: PlatformAlias
        get() = PlatformAlias.JVM

    override val repository: OceanWaterRepository
        get() = OceanWaterRepository()
}

actual fun getPlatform(): Platform = JVMPlatform()