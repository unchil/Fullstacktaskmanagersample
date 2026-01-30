package com.unchil.full_stack_task_manager_sample

import com.unchil.full_stack_task_manager_sample.module.configureDatabase
import com.unchil.full_stack_task_manager_sample.module.configureSerialization
import io.ktor.server.application.*
import com.unchil.full_stack_task_manager_sample.data.Repository

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module_Serialization(){
    LOGGER.info("Start Ktor Server")
    configureDatabase()
    configureSerialization(Repository())
}

