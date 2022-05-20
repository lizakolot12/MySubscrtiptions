package com.mits.subscription.data.db.dao

import androidx.room.*
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.model.Lesson

@Dao
interface LessonDao {

    @Insert
    suspend fun insert(lessonEntity: LessonEntity):Long

    @Delete
    suspend fun delete(lessonEntity: LessonEntity)

    @Query("SELECT * FROM lesson where subscription_id=:id")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getAllById(id: Long): List<Lesson>

    @Query("SELECT * FROM lesson where lId = :id")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getId(id: Long): Lesson

    @Update
    suspend fun updateLesson(lessonEntity: LessonEntity)
}
