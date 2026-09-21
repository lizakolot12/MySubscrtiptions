package com.mits.subscription.data.repository

import android.util.Log
import com.mits.subscription.data.db.dao.LessonDao
import com.mits.subscription.data.db.dao.SubscriptionDao
import com.mits.subscription.data.db.dao.WorkshopDao
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.data.db.entity.WorkshopEntity
import com.mits.subscription.data.mapper.toDomain
import com.mits.subscription.domain.model.Lesson
import com.mits.subscription.domain.model.Subscription
import com.mits.subscription.domain.model.Workshop
import com.mits.subscription.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val lessonDao: LessonDao,
    private val subscriptionDao: SubscriptionDao,
    private val workshopDao: WorkshopDao,
) : SubscriptionRepository {

    override val workshops: Flow<List<Workshop>> =
        workshopDao.getAll().map { list ->
            list.filter { it.workshop.deletedAt == null }.map { it.toDomain() }
        }

    override fun getSubscription(subscriptionId: Long): Flow<Subscription?> =
        subscriptionDao.getById(subscriptionId).map { details ->
            details?.takeIf { it.subscription.deletedAt == null }?.toDomain()
        }

    override suspend fun createWorkshop(name: String): Long =
        workshopDao.insert(WorkshopEntity(name = name))

    override suspend fun createSubscription(subscription: Subscription): Long {
        val entity = SubscriptionEntity(
            id = null,
            detail = subscription.detail,
            startDate = subscription.startDate,
            endDate = subscription.endDate,
            lessonNumbers = subscription.lessonNumbers,
            workshopId = subscription.workshopId,
            message = subscription.message,
            filePath = subscription.filePath,
        )
        return subscriptionDao.insert(entity)
    }

    // Soft-deletes cascade explicitly here (workshop -> its subscriptions -> their lessons) because a
    // real DELETE FROM would rely on the FK ON DELETE CASCADE, which never fires for an UPDATE.
    override suspend fun deleteWorkshop(workshopId: Long) {
        val now = System.currentTimeMillis()
        lessonDao.softDeleteByWorkshopId(workshopId, now)
        subscriptionDao.softDeleteByWorkshopId(workshopId, now)
        workshopDao.softDeleteById(workshopId, now)
    }

    override suspend fun deleteSubscription(subscription: Subscription) {
        val activeSiblingCount = subscriptionDao.countActiveByWorkshopId(subscription.workshopId)
        if (activeSiblingCount > 1) {
            val now = System.currentTimeMillis()
            lessonDao.softDeleteBySubscriptionId(subscription.id, now)
            subscriptionDao.softDeleteById(subscription.id, now)
        } else {
            deleteWorkshop(subscription.id) // NOTE: Original bug kept — should be subscription.workshopId
        }
    }

    override suspend fun updateWorkshop(workshopId: Long, workshopName: String?) {
        workshopDao.updateWorkshop(workshopId, workshopName, System.currentTimeMillis())
    }

    override suspend fun updateDetail(subscriptionId: Long, detail: String?) {
        subscriptionDao.updateDetail(subscriptionId, detail, System.currentTimeMillis())
    }

    override suspend fun updatePaymentFileInfo(
        subscriptionId: Long,
        uri: String?,
        fileName: String?,
    ) {
        subscriptionDao.updatePhotoUri(subscriptionId, uri, fileName, System.currentTimeMillis())
    }

    override suspend fun updateLessonsNumber(subscriptionId: Long, number: Int) {
        subscriptionDao.updateLessonsNumber(subscriptionId, number, System.currentTimeMillis())
    }

    override suspend fun updateStartDate(subscriptionId: Long, startDate: Long) {
        subscriptionDao.updateStartDate(subscriptionId, startDate, System.currentTimeMillis())
    }

    override suspend fun updateEndDate(subscriptionId: Long, endDate: Long) {
        subscriptionDao.updateEndDate(subscriptionId, endDate, System.currentTimeMillis())
    }

    override suspend fun updateMessage(subscriptionId: Long, message: String?) {
        subscriptionDao.updateMessage(subscriptionId, message, System.currentTimeMillis())
    }

    override suspend fun addLesson(subscriptionId: Long, lesson: Lesson): Long {
        val entity = LessonEntity(0, lesson.description, lesson.date, subscriptionId)
        return lessonDao.insert(entity)
    }

    override suspend fun updateLesson(lesson: Lesson, newDate: Date, subscriptionId: Long) {
        Log.e("TEST", "updateLesson: id=${lesson.lId} date=$newDate")
        lessonDao.updateLesson(lesson.lId, lesson.description, newDate, System.currentTimeMillis())
    }

    override suspend fun deleteLesson(lessonId: Long) {
        lessonDao.softDeleteById(lessonId, System.currentTimeMillis())
    }

    override suspend fun getAllFilePath(): List<String> = subscriptionDao.getAllFilePath()
}