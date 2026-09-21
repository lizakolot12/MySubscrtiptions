package com.mits.subscription.data.remote.dto

import kotlinx.serialization.Serializable

/** Mirrors mysubscriptionsback's sync DTOs exactly (SyncDtos.kt) — same field names, epoch-millis timestamps. */
@Serializable
data class WorkshopDto(
    val id: String,
    val name: String?,
    val updatedAt: Long,
    val deletedAt: Long? = null,
)

@Serializable
data class SubscriptionDto(
    val id: String,
    val workshopId: String,
    val detail: String?,
    val startDate: Long?,
    val endDate: Long?,
    val lessonNumbers: Int,
    val message: String?,
    val filePath: String?,
    val originFileName: String?,
    val updatedAt: Long,
    val deletedAt: Long? = null,
)

@Serializable
data class LessonDto(
    val id: String,
    val subscriptionId: String,
    val description: String?,
    val date: Long?,
    val updatedAt: Long,
    val deletedAt: Long? = null,
)

@Serializable
data class SyncPullResponse(
    val serverVersion: Long,
    val workshops: List<WorkshopDto>,
    val subscriptions: List<SubscriptionDto>,
    val lessons: List<LessonDto>,
)

@Serializable
data class SyncPushRequest(
    val workshops: List<WorkshopDto> = emptyList(),
    val subscriptions: List<SubscriptionDto> = emptyList(),
    val lessons: List<LessonDto> = emptyList(),
)

@Serializable
data class SyncPushResponse(
    val serverVersion: Long,
)
