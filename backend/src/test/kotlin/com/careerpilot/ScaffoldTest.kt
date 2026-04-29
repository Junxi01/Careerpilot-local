package com.careerpilot

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScaffoldTest {
    @Test
    fun `scaffold endpoint responds`() = testApplication {
        application { module() }
        val resp = client.get("/api/scaffold")
        assertEquals(HttpStatusCode.OK, resp.status)
        assertTrue(resp.bodyAsText().contains("careerpilot-backend"))
    }
}

