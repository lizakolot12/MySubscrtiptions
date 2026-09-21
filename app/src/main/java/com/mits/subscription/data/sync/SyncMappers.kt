package com.mits.subscription.data.sync

import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.data.db.entity.WorkshopEntity
import com.mits.subscription.data.remote.dto.LessonDto
import com.mits.subscription.data.remote.dto.SubscriptionDto
import com.mits.subscription.data.remote.dto.WorkshopDto

fun WorkshopEntity.toDto() = WorkshopDto(
    id = remoteId,
    name = name,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
)

fun SubscriptionEntity.toDto(workshopRemoteId: String) = SubscriptionDto(
    id = remoteId,
    workshopId = workshopRemoteId,
    detail = detail,
    startDate = startDate,
    endDate = endDate,
    lessonNumbers = lessonNumbers,
    message = message,
    filePath = filePath,
    originFileName = originFileName,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
)

fun LessonEntity.toDto(subscriptionRemoteId: String) = LessonDto(
    id = remoteId,
    subscriptionId = subscriptionRemoteId,
    description = description,
    date = date?.time,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
)
