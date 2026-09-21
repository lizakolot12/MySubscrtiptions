package com.mits.subscription.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthRequest(
    val idToken: String,
    val deviceLabel: String? = null,
)

@Serializable
data class RefreshRequest(
    val refreshToken: String,
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val userId: String,
    val email: String,
)
