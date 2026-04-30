package com.careerpilot

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val cfg = AppConfig.fromEnv(System.getenv())
    environment.log.info("Starting {} v{} on port {}", cfg.appName, cfg.version, cfg.port)
    environment.log.info(
        "DB config loaded (no connection yet): host={}, port={}, name={}, user={}",
        cfg.db.host,
        cfg.db.port,
        cfg.db.name,
        cfg.db.user,
    )

    install(CallLogging) {
        level = Level.INFO
    }

    install(CORS) {
        // Local dev only for now.
        anyHost()
        allowNonSimpleContentTypes = true
    }

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
        get("/health") { call.respond(HealthResponse(status = "ok")) }
        get("/api/version") { call.respond(VersionResponse(name = cfg.appName, version = cfg.version)) }
    }
}

@Serializable
data class HealthResponse(
    val status: String,
)

@Serializable
data class VersionResponse(
    val name: String,
    val version: String,
)

data class AppConfig(
    val appName: String,
    val version: String,
    val port: Int,
    val db: DbConfig,
) {
    companion object {
        fun fromEnv(env: Map<String, String>): AppConfig {
            val appName = env["APP_NAME"]?.takeIf { it.isNotBlank() } ?: "careerpilot-backend"
            val version = env["APP_VERSION"]?.takeIf { it.isNotBlank() } ?: "0.1.0"
            val port = env["BACKEND_PORT"]?.toIntOrNull() ?: 8080
            val db = DbConfig.fromEnv(env)
            return AppConfig(appName = appName, version = version, port = port, db = db)
        }
    }
}

data class DbConfig(
    val host: String,
    val port: Int,
    val name: String,
    val user: String,
    val password: String,
) {
    companion object {
        fun fromEnv(env: Map<String, String>): DbConfig {
            return DbConfig(
                host = env["DB_HOST"] ?: "localhost",
                port = env["DB_PORT"]?.toIntOrNull() ?: 3306,
                name = env["DB_NAME"] ?: "careerpilot",
                user = env["DB_USER"] ?: "careerpilot",
                password = env["DB_PASSWORD"] ?: "",
            )
        }
    }
}
