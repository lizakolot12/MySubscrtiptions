package com.mits.subscription.data.repository

import com.mits.subscription.data.db.dao.LessonDao
import com.mits.subscription.data.db.dao.SubscriptionDao
import com.mits.subscription.data.db.dao.WorkshopDao
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.data.db.entity.WorkshopEntity
import com.mits.subscription.data.remote.SyncApi
import com.mits.subscription.data.remote.dto.LessonDto
import com.mits.subscription.data.remote.dto.SubscriptionDto
import com.mits.subscription.data.remote.dto.SyncPullResponse
import com.mits.subscription.data.remote.dto.SyncPushRequest
import com.mits.subscription.data.remote.dto.WorkshopDto
import com.mits.subscription.data.sync.SyncPreferences
import com.mits.subscription.data.sync.toDto
import com.mits.subscription.domain.repository.SyncRepository
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val syncApi: SyncApi,
    private val workshopDao: WorkshopDao,
    private val subscriptionDao: SubscriptionDao,
    private val lessonDao: LessonDao,
    private val syncPreferences: SyncPreferences,
) : SyncRepository {

    override suspend fun sync(): Result<Unit> = runCatching {
        // Captured before reading any rows: an edit made during or after this cycle keeps
        // updatedAt >= syncStartedAt, so it's still `> lastPushAt` next time and won't be missed.
        val syncStartedAt = System.currentTimeMillis()

        val pushRequest = buildPushRequest(syncPreferences.lastPushAt())
        if (pushRequest.workshops.isNotEmpty() || pushRequest.subscriptions.isNotEmpty() || pushRequest.lessons.isNotEmpty()) {
            syncApi.push(pushRequest)
        }

        // Pulling since our last known server version also re-fetches the rows we just pushed above —
        // harmless, applyPull matches by remoteId and overwrites with the same values it just sent.
        val pullResponse = syncApi.pull(syncPreferences.lastPullVersion())
        applyPull(pullResponse)

        syncPreferences.saveSyncResult(
            pullVersion = pullResponse.serverVersion,
            pushAt = syncStartedAt,
            completedAt = System.currentTimeMillis(),
        )
    }

    private suspend fun buildPushRequest(since: Long): SyncPushRequest {
        val workshopDtos = workshopDao.getUpdatedSince(since).map { it.toDto() }
        val subscriptionDtos = subscriptionDao.getUpdatedSince(since).mapNotNull { entity ->
            val workshopRemoteId = workshopDao.getRemoteId(entity.workshopId) ?: return@mapNotNull null
            entity.toDto(workshopRemoteId)
        }
        val lessonDtos = lessonDao.getUpdatedSince(since).mapNotNull { entity ->
            val subscriptionRemoteId = subscriptionDao.getRemoteId(entity.subscriptionId) ?: return@mapNotNull null
            entity.toDto(subscriptionRemoteId)
        }
        return SyncPushRequest(workshops = workshopDtos, subscriptions = subscriptionDtos, lessons = lessonDtos)
    }

    /** Parent-before-child order (workshops, then subscriptions, then lessons): a child's Room FK
     * needs its parent's LOCAL row to already exist, resolved here via remoteId lookup. */
    private suspend fun applyPull(response: SyncPullResponse) {
        response.workshops.forEach { applyWorkshop(it) }
        response.subscriptions.forEach { applySubscription(it) }
        response.lessons.forEach { applyLesson(it) }
    }

    private suspend fun applyWorkshop(dto: WorkshopDto) {
        val existing = workshopDao.findByRemoteId(dto.id)
        if (existing != null) {
            workshopDao.applyRemote(existing.copy(name = dto.name, updatedAt = dto.updatedAt, deletedAt = dto.deletedAt))
        } else {
            workshopDao.insert(
                WorkshopEntity(name = dto.name, remoteId = dto.id, updatedAt = dto.updatedAt, deletedAt = dto.deletedAt),
            )
        }
    }

    private suspend fun applySubscription(dto: SubscriptionDto) {
        val workshopLocalId = workshopDao.findByRemoteId(dto.workshopId)?.id ?: return
        val existing = subscriptionDao.findByRemoteId(dto.id)
        if (existing != null) {
            subscriptionDao.updateSubscription(
                existing.copy(
                    workshopId = workshopLocalId,
                    detail = dto.detail,
                    startDate = dto.startDate,
                    endDate = dto.endDate,
                    lessonNumbers = dto.lessonNumbers,
                    message = dto.message,
                    filePath = dto.filePath,
                    originFileName = dto.originFileName,
                    updatedAt = dto.updatedAt,
                    deletedAt = dto.deletedAt,
                ),
            )
        } else {
            subscriptionDao.insert(
                SubscriptionEntity(
                    id = null,
                    detail = dto.detail,
                    startDate = dto.startDate,
                    endDate = dto.endDate,
                    lessonNumbers = dto.lessonNumbers,
                    workshopId = workshopLocalId,
                    message = dto.message,
                    filePath = dto.filePath,
                    originFileName = dto.originFileName,
                    remoteId = dto.id,
                    updatedAt = dto.updatedAt,
                    deletedAt = dto.deletedAt,
                ),
            )
        }
    }

    private suspend fun applyLesson(dto: LessonDto) {
        val subscriptionLocalId = subscriptionDao.findByRemoteId(dto.subscriptionId)?.id ?: return
        val existing = lessonDao.findByRemoteId(dto.id)
        if (existing != null) {
            lessonDao.applyRemote(
                existing.copy(
                    subscriptionId = subscriptionLocalId,
                    description = dto.description,
                    date = dto.date?.let(::Date),
                    updatedAt = dto.updatedAt,
                    deletedAt = dto.deletedAt,
                ),
            )
        } else {
            lessonDao.insert(
                LessonEntity(
                    lId = 0,
                    description = dto.description,
                    date = dto.date?.let(::Date),
                    subscriptionId = subscriptionLocalId,
                    remoteId = dto.id,
                    updatedAt = dto.updatedAt,
                    deletedAt = dto.deletedAt,
                ),
            )
        }
    }
}
