package com.unchil.full_stack_task_manager_sample



interface Platform {
    val name: String
    val alias: PlatformAlias

    val repository: OceanWaterRepository
}

expect fun getPlatform(): Platform

