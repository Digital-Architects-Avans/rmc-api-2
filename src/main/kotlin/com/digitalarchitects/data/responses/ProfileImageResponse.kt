package com.digitalarchitects.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class ProfileImageResponse (
    val profileImageSrc: String
)