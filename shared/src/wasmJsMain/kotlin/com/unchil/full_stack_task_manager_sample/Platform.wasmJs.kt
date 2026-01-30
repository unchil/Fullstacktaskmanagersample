package com.unchil.full_stack_task_manager_sample

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"

    override val alias: PlatformAlias
        get() = PlatformAlias.WASM
}

actual fun getPlatform(): Platform = WasmPlatform()