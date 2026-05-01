package com.careerpilot.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import java.util.Date

class JwtService(private val cfg: AuthConfig) {
    private val algorithm: Algorithm = Algorithm.HMAC256(cfg.jwtSecret)

    val verifier: JWTVerifier =
        JWT
            .require(algorithm)
            .withIssuer(cfg.issuer)
            .withAudience(cfg.audience)
            .build()

    fun generateToken(userId: Long): String {
        val now = Instant.now()
        val exp = now.plusSeconds(cfg.ttlSeconds)
        return JWT.create()
            .withIssuer(cfg.issuer)
            .withAudience(cfg.audience)
            .withSubject(userId.toString())
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(exp))
            .sign(algorithm)
    }
}

