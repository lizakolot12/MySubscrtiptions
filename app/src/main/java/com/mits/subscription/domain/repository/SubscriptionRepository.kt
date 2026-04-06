package com.mits.subscription.domain.repository

import com.mits.subscription.domain.model.Lesson
import com.mits.subscription.domain.model.Subscription
import com.mits.subscription.domain.model.Workshop
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface SubscriptionRepository {

    val workshops: Flow<List<Workshop>>

    fun getSubscription(subscriptionId: Long): Flow<Subscription?>

    suspend fun createWorkshop(name: String): Long

    suspend fun createSubscription(subscription: Subscription): Long

    suspend fun deleteWorkshop(workshopId: Long)

    suspend fun deleteSubscription(subscription: Subscription)

    suspend fun updateWorkshop(workshopId: Long, workshopName: String?)

    suspend fun updateDetail(subscriptionId: Long, detail: String?)

    suspend fun updatePaymentFileInfo(subscriptionId: Long, uri: String?, fileName: String?)

    suspend fun updateLessonsNumber(subscriptionId: Long, number: Int)

    suspend fun updateStartDate(subscriptionId: Long, startDate: Long)

    suspend fun updateEndDate(subscriptionId: Long, endDate: Long)

    suspend fun updateMessage(subscriptionId: Long, message: String?)

    suspend fun addLesson(subscriptionId: Long, lesson: Lesson): Long

    suspend fun updateLesson(lesson: Lesson, newDate: Date, subscriptionId: Long)

    suspend fun deleteLesson(lessonId: Long)

    suspend fun getAllFilePath(): List<String>
}