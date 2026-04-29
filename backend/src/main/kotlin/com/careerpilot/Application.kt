package com.careerpilot

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = false
                ignoreUnknownKeys = true
                explicitNulls = false
            },
        )
    }

    routing {
        get("/") { call.respond(ScaffoldInfo(name = "careerpilot-backend", status = "scaffold")) }
        get("/api/scaffold") { call.respond(ScaffoldInfo(name = "careerpilot-backend", status = "ok")) }
        post("/api/scaffold") { call.respond(ScaffoldInfo(name = "careerpilot-backend", status = "ok")) }
    }
}

@Serializable
data class ScaffoldInfo(
    val name: String,
    val status: String,
)
