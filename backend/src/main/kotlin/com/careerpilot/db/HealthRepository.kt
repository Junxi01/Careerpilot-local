package com.careerpilot.db

/**
 * Minimal repository layer (Day 5).
 *
 * Keep DB access behind a small abstraction so business APIs can later depend on repositories
 * rather than raw JDBC calls.
 */
class HealthRepository(private val db: DatabaseModule) {
    fun selectOne(): Int {
        db.openConnection().use { conn ->
            conn.createStatement().use { st ->
                st.executeQuery("SELECT 1").use { rs ->
                    if (!rs.next()) throw IllegalStateException("SELECT 1 returned no rows")
                    return rs.getInt(1)
                }
            }
        }
    }
}

