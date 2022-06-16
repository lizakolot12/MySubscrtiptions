package com.mits.subscription.data.repo

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.mits.subscription.data.db.dao.FolderDao
import com.mits.subscription.data.db.dao.LessonDao
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.dao.SubscriptionDao
import com.mits.subscription.data.db.entity.FolderEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.model.Folder
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import java.util.*

class SubscriptionRepository(
    private val lessonDao: LessonDao,
    private val subscriptionDao: SubscriptionDao,
    private val folderDao: FolderDao
) {

    val subsFolders: LiveData<List<Folder>> = folderDao.getAll()

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

    suspend fun get(subscriptionId: Long): Subscription {
        return subscriptionDao.getById(subscriptionId)
    }

    suspend fun update(subscription:Subscription){
        lessonDao.deleteBySubscriptionId(subscription.id)
        subscription.lessons?.forEach {
            lessonDao.insert(LessonEntity( 0, it.description, it.date, subscription.id))
        }
        return subscriptionDao.updateSubscription(convert(subscription))
    }

    suspend fun deleteSubscription(subscription: Subscription) {
        subscriptionDao.delete(
            convert(subscription)
        )
    }

    private fun convert(subscription: Subscription):SubscriptionEntity{
        return SubscriptionEntity(
            subscription.id,
            subscription.name,
            subscription.startDate,
            subscription.endDate,
            subscription.lessonNumbers,
            subscription.description
        )
    }

    suspend fun createFolder(name: String) {
        folderDao.insert(FolderEntity(0, name))
    }

}