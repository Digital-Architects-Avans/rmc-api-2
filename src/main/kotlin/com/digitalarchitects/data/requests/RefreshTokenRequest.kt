package com.digitalarchitects.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    val token: String,
)