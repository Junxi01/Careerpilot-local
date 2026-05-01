package com.careerpilot.auth

data class AuthConfig(
    val jwtSecret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val ttlSeconds: Long,
) {
    companion object {
        fun fromEnv(env: Map<String, String>): AuthConfig {
            val secret = env["JWT_SECRET"]?.trim().orEmpty()
            if (secret.isBlank() || secret.startsWith("change-me")) {
                throw IllegalArgumentException(
                    "JWT_SECRET is required (set it in .env). Refusing to start with an empty/default secret.",
                )
            }
            val issuer = env["JWT_ISSUER"]?.takeIf { it.isNotBlank() } ?: "careerpilot-local"
            val audience = env["JWT_AUDIENCE"]?.takeIf { it.isNotBlank() } ?: "careerpilot-local"
            val realm = env["JWT_REALM"]?.takeIf { it.isNotBlank() } ?: "careerpilot"
            val ttl = env["JWT_TTL_SECONDS"]?.toLongOrNull() ?: 60L * 60L * 24L * 7L
            return AuthConfig(jwtSecret = secret, issuer = issuer, audience = audience, realm = realm, ttlSeconds = ttl)
        }
    }
}

