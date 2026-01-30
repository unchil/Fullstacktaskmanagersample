package com.unchil.full_stack_task_manager_sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform