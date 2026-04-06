package com.mits.subscription.domain.usecase

import com.mits.subscription.data.file.FileHandler
import com.mits.subscription.domain.repository.SubscriptionRepository
import javax.inject.Inject

class CleanUnreferencedFilesUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val fileHandler: FileHandler,
) {
    suspend operator fun invoke() {
        fileHandler.clean(subscriptionRepository.getAllFilePath())
    }
}