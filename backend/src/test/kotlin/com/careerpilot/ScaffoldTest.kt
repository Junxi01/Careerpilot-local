package com.careerpilot

import io.ktor.client.request.header
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScaffoldTest {
    private val h2Env =
        mapOf(
            "DB_JDBC_URL" to "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1",
            "DB_USER" to "sa",
            "DB_PASSWORD" to "",
            "JWT_SECRET" to "test-secret",
        )

    @Test
    fun `health endpoint returns ok`() = testApplication {
        application { moduleWithEnv(h2Env) }
        val resp = client.get("/health")
        assertEquals(HttpStatusCode.OK, resp.status)
        assertTrue(resp.bodyAsText().contains("\"status\":\"ok\""))
    }

    @Test
    fun `db health returns down when unreachable`() = testApplication {
        application {
            moduleWithEnv(
                mapOf(
                    "DB_HOST" to "127.0.0.1",
                    "DB_PORT" to "65000",
                    "JWT_SECRET" to "test-secret",
                ),
            )
        }
        val resp = client.get("/health/db")
        assertEquals(HttpStatusCode.ServiceUnavailable, resp.status)
        assertTrue(resp.bodyAsText().contains("\"status\":\"down\""))
        assertTrue(resp.bodyAsText().contains("\"error\""))
    }

    @Test
    fun `register and login and me flow`() = testApplication {
        application { moduleWithEnv(h2Env) }

        // Create users table for H2 test DB
        java.sql.DriverManager.getConnection(h2Env["DB_JDBC_URL"], "sa", "").use { c ->
            c.createStatement().use { st ->
                st.execute(
                    """
                    CREATE TABLE users (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      email VARCHAR(255) NOT NULL,
                      password_hash VARCHAR(255) NULL,
                      display_name VARCHAR(190) NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    """.trimIndent(),
                )
                st.execute("CREATE UNIQUE INDEX uq_users_email ON users(email);")
            }
        }

        val register = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"a@b.com","password":"password123","displayName":"A"}""")
        }
        assertEquals(HttpStatusCode.Created, register.status)
        val regBody = register.bodyAsText()
        assertTrue(regBody.contains("\"success\":true"))
        assertTrue(regBody.contains("\"token\""))
        // Verify password is not stored in plaintext
        java.sql.DriverManager.getConnection(h2Env["DB_JDBC_URL"], "sa", "").use { c ->
            c.createStatement().use { st ->
                st.executeQuery("SELECT password_hash FROM users WHERE email='a@b.com'").use { rs ->
                    assertTrue(rs.next())
                    val hash = rs.getString(1)
                    assertTrue(hash != "password123")
                    assertTrue(hash.startsWith("$2"))
                }
            }
        }

        val dup = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"a@b.com","password":"password123"}""")
        }
        assertEquals(HttpStatusCode.Conflict, dup.status)

        val badPw = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"a@b.com","password":"wrongpass"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, badPw.status)

        val login = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"a@b.com","password":"password123"}""")
        }
        assertEquals(HttpStatusCode.OK, login.status)
        val loginBody = login.bodyAsText()
        val token =
            Regex("\"token\"\\s*:\\s*\"([^\"]+)\"").find(loginBody)?.groupValues?.get(1)
                ?: error("Missing token in login response: $loginBody")

        val meNoToken = client.get("/api/me")
        assertEquals(HttpStatusCode.Unauthorized, meNoToken.status)

        val me = client.get("/api/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, me.status)
        assertTrue(me.bodyAsText().contains("\"email\":\"a@b.com\""))
    }
}

