package com.mits.subscription.presenatation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mits.subscription.domain.clean.FileCleaner
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val fileCleaner: FileCleaner
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        fileCleaner.cleanUpUnreferencedFiles()
        return Result.success()
    }
}