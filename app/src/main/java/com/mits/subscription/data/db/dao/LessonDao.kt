package com.mits.subscription.data.db.dao

import androidx.room.*
import com.mits.subscription.data.db.entity.LessonEntity
import java.util.Date

@Dao
interface LessonDao {

    @Insert
    suspend fun insert(lessonEntity: LessonEntity):Long

    @Delete
    suspend fun delete(lessonEntity: LessonEntity)

    @Query("DELETE FROM lesson where lId=:lessonId")
    suspend fun deleteByLessonId(lessonId:Long)

    @Query("DELETE FROM lesson where subscription_id=:id")
    @RewriteQueriesToDropUnusedColumns
    suspend fun deleteBySubscriptionId(id: Long)

    @Query("SELECT * FROM lesson where lId = :id")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getId(id: Long): LessonEntity

    // Deliberately a targeted query, not @Update on the whole entity: see WorkshopDao.updateWorkshop
    // for why (would silently regenerate remoteId).
    @Query("UPDATE lesson SET description = :description, date = :date, updatedAt = :updatedAt WHERE lId = :id")
    suspend fun updateLesson(id: Long, description: String?, date: Date?, updatedAt: Long)

    @Query("SELECT * FROM lesson WHERE remoteId = :remoteId LIMIT 1")
    suspend fun findByRemoteId(remoteId: String): LessonEntity?

    @Query("SELECT * FROM lesson WHERE updatedAt > :since")
    suspend fun getUpdatedSince(since: Long): List<LessonEntity>

    // Used by sync to apply server-authoritative rows: see SubscriptionDao.updateSubscription for why
    // this is safe as a whole-entity @Update while updateLesson (above) deliberately isn't.
    @Update
    suspend fun applyRemote(lessonEntity: LessonEntity)
}
