package com.mits.subscription.domain.repository

interface SyncRepository {
    /** Pushes local changes since the last successful sync, then pulls remote changes. */
    suspend fun sync(): Result<Unit>
}
