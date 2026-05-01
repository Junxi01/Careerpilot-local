package com.careerpilot.auth

import com.careerpilot.repo.PublicUser
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String? = null,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class AuthPayload(
    val token: String,
    val user: PublicUser,
)

@Serializable
data class MeResponse(
    val user: PublicUser,
)

