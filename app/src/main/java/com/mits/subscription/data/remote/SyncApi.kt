package com.mits.subscription.data.remote

import com.mits.subscription.data.remote.dto.SyncPullResponse
import com.mits.subscription.data.remote.dto.SyncPushRequest
import com.mits.subscription.data.remote.dto.SyncPushResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** Mirrors mysubscriptionsback's SyncController (api/v1/sync/changes). Requires a bearer token — see AuthInterceptor. */
interface SyncApi {

    @GET("api/v1/sync/changes")
    suspend fun pull(@Query("since") since: Long): SyncPullResponse

    @POST("api/v1/sync/changes")
    suspend fun push(@Body request: SyncPushRequest): SyncPushResponse
}
