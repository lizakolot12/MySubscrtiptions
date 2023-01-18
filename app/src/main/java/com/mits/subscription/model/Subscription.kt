package com.mits.subscription.model

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.mits.subscription.data.db.entity.LessonEntity
import java.util.*

data class Subscription(

    @ColumnInfo(name="sub_id")
    var id: Long,
    var detail: String? = null,
    var startDate: Date?,
    var endDate: Date?,
    var lessonNumbers: Int = 0,

    @Relation(
        parentColumn = "sub_id",
        entityColumn = "subscription_id",
        entity = LessonEntity::class
    )
    var lessons: List<Lesson>? = null,

    @ColumnInfo(name="workshop_id")
    var workshopId:Long
)