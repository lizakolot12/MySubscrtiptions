package com.mits.subscription.presenatation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.mits.subscription.domain.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result =
        syncRepository.sync().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() },
        )

    companion object {
        private const val PERIODIC_WORK_NAME = "periodic_sync"
        private const val MANUAL_WORK_NAME = "manual_sync"
        private const val SYNC_INTERVAL_HOURS = 6L

        private fun networkConstraints() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        /** Safe to call on every app start: KEEP means an already-scheduled period isn't reset. */
        fun schedulePeriodic(workManager: WorkManager) {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(SYNC_INTERVAL_HOURS, TimeUnit.HOURS)
                .setConstraints(networkConstraints())
                .build()
            workManager.enqueueUniquePeriodicWork(PERIODIC_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }

        /** For a manual "sync now" action; replaces any pending manual request rather than stacking them. */
        fun triggerNow(workManager: WorkManager) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(networkConstraints())
                .build()
            workManager.enqueueUniqueWork(MANUAL_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }
    }
}
