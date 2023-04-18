package com.mits.subscription.model

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.mits.subscription.data.db.entity.LessonEntity
import java.util.*

data class Subscription(
    @ColumnInfo(name="sub_id")
    val id: Long,
    val detail: String? = null,
    val startDate: Date?,
    val endDate: Date?,
    val lessonNumbers: Int = 0,

    @Relation(
        parentColumn = "sub_id",
        entityColumn = "subscription_id",
        entity = LessonEntity::class
    )
    val lessons: List<Lesson>? = null,

    @ColumnInfo(name="workshop_id")
    val workshopId:Long,

    val message:String?
)