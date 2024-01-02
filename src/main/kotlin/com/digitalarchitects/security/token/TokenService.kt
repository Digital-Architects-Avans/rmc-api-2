package com.digitalarchitects.security.token

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String

    fun getUserIdFromToken(token: String): String?
}