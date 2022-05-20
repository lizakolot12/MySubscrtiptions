package com.mits.subscription.data.repo

import androidx.lifecycle.LiveData
import com.mits.subscription.data.db.dao.LessonDao
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.dao.SubscriptionDao
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription

class SubscriptionRepository(
    private val lessonDao: LessonDao,
    private val subscriptionDao: SubscriptionDao
) {

    val subscriptions:LiveData<List<Subscription>>  = subscriptionDao.getAll()

    suspend fun createSubscription(subscription: Subscription): Long {
        val subscriptionEntity = SubscriptionEntity(
            subscription.id,
            subscription.name,
            subscription.startDate,
            subscription.endDate,
            subscription.lessonNumbers,
            subscription.description,
        )
        return subscriptionDao.insert(subscriptionEntity)
    }

    suspend fun addLesson(subscriptionId: Long, lesson: Lesson): Long {
        val lessonEntity = LessonEntity(0, lesson.description, lesson.date, subscriptionId)
        return lessonDao.insert(lessonEntity)
    }

}