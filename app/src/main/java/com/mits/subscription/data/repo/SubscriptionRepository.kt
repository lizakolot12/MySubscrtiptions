package com.mits.subscription.data.repo

import androidx.lifecycle.LiveData
import com.mits.subscription.data.db.dao.WorkshopDao
import com.mits.subscription.data.db.dao.LessonDao
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.dao.SubscriptionDao
import com.mits.subscription.data.db.entity.WorkshopEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.model.Workshop
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription

class SubscriptionRepository(
    private val lessonDao: LessonDao,
    private val subscriptionDao: SubscriptionDao,
    private val workshopDao: WorkshopDao
) {

    val workshops: LiveData<List<Workshop>> = workshopDao.getAll()

    suspend fun createSubscription(subscription: Subscription): Long {
        val subscriptionEntity = SubscriptionEntity(
            null,
            subscription.detail,
            subscription.startDate,
            subscription.endDate,
            subscription.lessonNumbers,
            subscription.workshopId
        )
        return subscriptionDao.insert(subscriptionEntity)
    }

    suspend fun addLesson(subscriptionId: Long, lesson: Lesson): Long {
        val lessonEntity = LessonEntity(0, lesson.description, lesson.date, subscriptionId)
        return lessonDao.insert(lessonEntity)
    }

    suspend fun getSubscription(subscriptionId: Long): Subscription {
        return subscriptionDao.getById(subscriptionId)
    }

    suspend fun getWorkshop(workshopId: Long): Workshop {
        return workshopDao.getById(workshopId)
    }

    suspend fun update(subscription: Subscription, workshopName: String?) {
        lessonDao.deleteBySubscriptionId(subscription.id)
        subscription.lessons?.forEach {
            lessonDao.insert(LessonEntity(0, it.description, it.date, subscription.id))
        }
        workshopDao.updateWorkshop(WorkshopEntity(subscription.workshopId, workshopName))
        return subscriptionDao.updateSubscription(convert(subscription))
    }

    suspend fun deleteSubscription(subscription: Subscription) {
        subscriptionDao.delete(
            convert(subscription)
        )
    }

    private fun convert(subscription: Subscription): SubscriptionEntity {
        return SubscriptionEntity(
            subscription.id,
            subscription.detail,
            subscription.startDate,
            subscription.endDate,
            subscription.lessonNumbers,
            subscription.workshopId
        )
    }

    suspend fun createWorkshop(name: String) = workshopDao.insert(WorkshopEntity(name = name))


    suspend fun deleteFolder(workshop: Workshop) {
        workshopDao.delete(WorkshopEntity(workshop.id, workshop.name))
    }

}