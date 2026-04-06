package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.repository.SubscriptionRepository
import javax.inject.Inject

class DeleteLessonUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(lessonId: Long) = repository.deleteLesson(lessonId)
}