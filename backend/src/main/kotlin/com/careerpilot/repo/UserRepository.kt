package com.careerpilot.repo

import com.careerpilot.db.DatabaseModule
import kotlinx.serialization.Serializable
import java.sql.Statement

data class User(
    val id: Long,
    val email: String,
    val passwordHash: String?,
    val displayName: String?,
) {
    fun toPublic(): PublicUser = PublicUser(id = id, email = email, displayName = displayName)
}

@Serializable
data class PublicUser(
    val id: Long,
    val email: String,
    val displayName: String? = null,
)

class UserRepository(private val db: DatabaseModule) {
    fun findByEmail(email: String): User? {
        db.openConnection().use { conn ->
            conn.prepareStatement(
                "SELECT id, email, password_hash, display_name FROM users WHERE email = ? LIMIT 1",
            ).use { ps ->
                ps.setString(1, email)
                ps.executeQuery().use { rs ->
                    if (!rs.next()) return null
                    return User(
                        id = rs.getLong("id"),
                        email = rs.getString("email"),
                        passwordHash = rs.getString("password_hash"),
                        displayName = rs.getString("display_name"),
                    )
                }
            }
        }
    }

    fun findById(id: Long): User? {
        db.openConnection().use { conn ->
            conn.prepareStatement(
                "SELECT id, email, password_hash, display_name FROM users WHERE id = ? LIMIT 1",
            ).use { ps ->
                ps.setLong(1, id)
                ps.executeQuery().use { rs ->
                    if (!rs.next()) return null
                    return User(
                        id = rs.getLong("id"),
                        email = rs.getString("email"),
                        passwordHash = rs.getString("password_hash"),
                        displayName = rs.getString("display_name"),
                    )
                }
            }
        }
    }

    fun insert(email: String, passwordHash: String, displayName: String?): User {
        db.openConnection().use { conn ->
            conn.prepareStatement(
                "INSERT INTO users (email, password_hash, display_name) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS,
            ).use { ps ->
                ps.setString(1, email)
                ps.setString(2, passwordHash)
                ps.setString(3, displayName)
                ps.executeUpdate()
                ps.generatedKeys.use { keys ->
                    if (!keys.next()) error("Insert user: missing generated key")
                    val id = keys.getLong(1)
                    return User(id = id, email = email, passwordHash = passwordHash, displayName = displayName)
                }
            }
        }
    }
}

