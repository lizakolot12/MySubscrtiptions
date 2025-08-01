package com.mits.subscription.domain.clean

import com.mits.subscription.data.repo.FileHandler
import com.mits.subscription.data.repo.SubscriptionRepository
import javax.inject.Inject

class FileCleaner @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val fileHandler: FileHandler
) {
    suspend fun cleanUpUnreferencedFiles() {
        fileHandler.clean(subscriptionRepository.getAllFilePath())
    }
}