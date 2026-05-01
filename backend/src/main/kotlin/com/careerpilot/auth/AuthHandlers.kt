package com.careerpilot.auth

import com.careerpilot.repo.UserRepository
import io.ktor.http.HttpStatusCode

sealed class AuthResult<out T> {
    data class Ok<T>(val payload: T) : AuthResult<T>()
    data class Err(val status: HttpStatusCode, val code: String, val message: String) : AuthResult<Nothing>()
}

object AuthHandlers {
    fun register(req: RegisterRequest, users: UserRepository, jwt: JwtService): AuthResult<AuthPayload> {
        val email = req.email.trim().lowercase()
        if (email.isBlank()) return AuthResult.Err(HttpStatusCode.BadRequest, "invalid_email", "Email is required")
        if (req.password.length < 8) return AuthResult.Err(HttpStatusCode.BadRequest, "weak_password", "Password must be at least 8 characters")

        val existing = users.findByEmail(email)
        if (existing != null) return AuthResult.Err(HttpStatusCode.Conflict, "email_taken", "Email is already registered")

        val hash = PasswordHasher.hash(req.password)
        val user = users.insert(email = email, passwordHash = hash, displayName = req.displayName?.trim()?.takeIf { it.isNotBlank() })
        val token = jwt.generateToken(user.id)
        return AuthResult.Ok(AuthPayload(token = token, user = user.toPublic()))
    }

    fun login(req: LoginRequest, users: UserRepository, jwt: JwtService): AuthResult<AuthPayload> {
        val email = req.email.trim().lowercase()
        if (email.isBlank()) return AuthResult.Err(HttpStatusCode.BadRequest, "invalid_email", "Email is required")

        val user = users.findByEmail(email) ?: return AuthResult.Err(HttpStatusCode.Unauthorized, "invalid_credentials", "Invalid email or password")
        val hash = user.passwordHash ?: return AuthResult.Err(HttpStatusCode.Unauthorized, "invalid_credentials", "Invalid email or password")

        val ok = PasswordHasher.verify(req.password, hash)
        if (!ok) return AuthResult.Err(HttpStatusCode.Unauthorized, "invalid_credentials", "Invalid email or password")

        val token = jwt.generateToken(user.id)
        return AuthResult.Ok(AuthPayload(token = token, user = user.toPublic()))
    }
}

