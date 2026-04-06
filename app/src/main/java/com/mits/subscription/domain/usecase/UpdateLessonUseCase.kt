package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.model.Lesson
import com.mits.subscription.domain.repository.SubscriptionRepository
import java.util.Date
import javax.inject.Inject

class UpdateLessonUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(lesson: Lesson, newDate: Date, subscriptionId: Long) =
        repository.updateLesson(lesson, newDate, subscriptionId)
}