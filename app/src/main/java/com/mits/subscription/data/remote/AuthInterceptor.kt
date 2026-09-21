package com.mits.subscription.data.remote

import com.mits.subscription.data.auth.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** Attaches the stored access token to every request except the auth endpoints themselves. */
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenStore.accessToken
        val isAuthEndpoint = original.url.encodedPath.contains("/api/v1/auth/")
        val request = if (token != null && !isAuthEndpoint) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}
