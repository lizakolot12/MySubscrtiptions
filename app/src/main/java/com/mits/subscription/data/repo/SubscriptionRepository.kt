package com.mits.subscription.data.repo

import androidx.lifecycle.LiveData
import com.mits.subscription.data.db.dao.LessonDao
import com.mits.subscription.data.db.dao.SubscriptionDao
import com.mits.subscription.data.db.dao.WorkshopDao
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.data.db.entity.WorkshopEntity
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import com.mits.subscription.model.Workshop
import kotlinx.coroutines.flow.Flow
import java.util.*

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
            subscription.workshopId,
            subscription.message
        )
        return subscriptionDao.insert(subscriptionEntity)
    }

    suspend fun addLesson(subscriptionId: Long, lesson: Lesson): Long {
        val lessonEntity = LessonEntity(0, lesson.description, lesson.date, subscriptionId)
        return lessonDao.insert(lessonEntity)
    }

    suspend fun updateLesson(lesson: Lesson, newCalendar: Calendar, subscriptionId: Long) {
        val lessonEntity =
            LessonEntity(lesson.lId, lesson.description, newCalendar.time, subscriptionId)
        lessonDao.updateLesson(lessonEntity)
    }

    fun getSubscription(subscriptionId: Long): Flow<Subscription?> {
        return subscriptionDao.getById(subscriptionId)
    }

    suspend fun getWorkshop(workshopId: Long): Workshop {
        return workshopDao.getById(workshopId)
    }

    suspend fun update(subscription: Subscription) {
        lessonDao.deleteBySubscriptionId(subscription.id)
        subscription.lessons?.forEach {
            lessonDao.insert(LessonEntity(0, it.description, it.date, subscription.id))
        }
        return subscriptionDao.updateSubscription(convert(subscription))
    }

    suspend fun updateWorkshop(workshopId: Long, workshopName: String?) {
        workshopDao.updateWorkshop(WorkshopEntity(workshopId, workshopName))
    }


    suspend fun deleteWorkshop(subscription: Subscription) {
        workshopDao.deleteById(subscription.workshopId)
    }

    suspend fun deleteLesson(lesson: Lesson) {
        lessonDao.deleteByLessonId(lesson.lId)
    }

    suspend fun deleteSubscription(subscription: Subscription) {
        val currentWorkshop = workshopDao.getById(subscription.workshopId)
        if ((currentWorkshop.subscriptions?.size ?: 0) > 1) {
            subscriptionDao.deleteById(subscription.id)
        } else {
            deleteWorkshop(subscription)
        }
    }

    private fun convert(subscription: Subscription): SubscriptionEntity {
        return SubscriptionEntity(
            subscription.id,
            subscription.detail,
            subscription.startDate,
            subscription.endDate,
            subscription.lessonNumbers,
            subscription.workshopId,
            subscription.message
        )
    }

    suspend fun createWorkshop(name: String) = workshopDao.insert(WorkshopEntity(name = name))

    suspend fun addMessage(id: Long, message: String?) {
        subscriptionDao.updateMessage(id, message)
    }
}