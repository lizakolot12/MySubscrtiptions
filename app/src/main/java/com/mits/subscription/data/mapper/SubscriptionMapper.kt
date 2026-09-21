package com.mits.subscription.data.mapper

import com.mits.subscription.data.db.model.SubscriptionWithDetails
import com.mits.subscription.domain.model.Subscription
import com.mits.subscription.domain.model.WorkshopInfo

fun SubscriptionWithDetails.toDomain() = Subscription(
    id = subscription.id ?: 0L,
    detail = subscription.detail,
    startDate = subscription.startDate,
    endDate = subscription.endDate,
    lessonNumbers = subscription.lessonNumbers,
    lessons = lessons.filter { it.deletedAt == null }.map { it.toDomain() },
    workshop = workshop?.let { WorkshopInfo(it.id ?: 0L, it.name ?: "") },
    workshopId = subscription.workshopId,
    message = subscription.message,
    filePath = subscription.filePath,
    originFileName = subscription.originFileName,
)