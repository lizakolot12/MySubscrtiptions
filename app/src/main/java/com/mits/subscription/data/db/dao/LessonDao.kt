package com.mits.subscription.data.db.dao

import androidx.room.*
import com.mits.subscription.data.db.entity.LessonEntity

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

    @Update
    suspend fun updateLesson(lessonEntity: LessonEntity)
}
