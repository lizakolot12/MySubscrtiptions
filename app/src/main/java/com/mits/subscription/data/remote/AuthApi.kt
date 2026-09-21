package com.mits.subscription.data.remote

import com.mits.subscription.data.remote.dto.AuthResponse
import com.mits.subscription.data.remote.dto.GoogleAuthRequest
import com.mits.subscription.data.remote.dto.RefreshRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/** Mirrors mysubscriptionsback's AuthController (api/v1/auth/*). */
interface AuthApi {

    @POST("api/v1/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequest): AuthResponse

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): AuthResponse

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body request: RefreshRequest): Response<Unit>
}
