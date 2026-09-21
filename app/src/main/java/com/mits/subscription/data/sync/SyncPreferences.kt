package com.mits.subscription.data.sync

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(name = "sync_prefs")

/**
 * Two independent watermarks, both needed because they're not comparable to each other:
 * - [lastPullVersion]: the server's own monotonic `version` counter, for `GET /sync/changes?since=`.
 * - [lastPushAt]: this device's wall-clock time, for selecting local rows with `updatedAt > lastPushAt`
 *   to push next. Captured at the START of a sync (before reading rows), so an edit made during or
 *   after this cycle is still `> lastPushAt` and gets picked up next time.
 */
@Singleton
class SyncPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.syncDataStore
    private val lastPullVersionKey = longPreferencesKey("last_pull_version")
    private val lastPushAtKey = longPreferencesKey("last_push_at")
    private val lastSyncCompletedAtKey = longPreferencesKey("last_sync_completed_at")

    val lastSyncCompletedAt: Flow<Long?> = dataStore.data.map { it[lastSyncCompletedAtKey] }

    suspend fun lastPullVersion(): Long = dataStore.data.map { it[lastPullVersionKey] ?: 0L }.first()

    suspend fun lastPushAt(): Long = dataStore.data.map { it[lastPushAtKey] ?: 0L }.first()

    suspend fun saveSyncResult(pullVersion: Long, pushAt: Long, completedAt: Long) {
        dataStore.edit {
            it[lastPullVersionKey] = pullVersion
            it[lastPushAtKey] = pushAt
            it[lastSyncCompletedAtKey] = completedAt
        }
    }
}
