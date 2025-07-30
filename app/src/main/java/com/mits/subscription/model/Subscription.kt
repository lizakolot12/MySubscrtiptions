package com.mits.subscription.model

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.entity.WorkshopEntity
import java.util.Date


data class Subscription(
    @ColumnInfo(name="sub_id")
    val id: Long,
    val detail: String? = null,
    val startDate: Long?,
    val endDate: Long?,
    val lessonNumbers: Int = 0,

    @Relation(
        parentColumn = "sub_id",
        entityColumn = "subscription_id",
        entity = LessonEntity::class
    )
    val lessons: List<Lesson>? = null,

    @Relation(
        parentColumn = "workshop_id",
        entityColumn = "workshop_id",
        entity = WorkshopEntity::class
    )
    val workshop:WorkshopBase? = null,

    @ColumnInfo(name="workshop_id")
    val workshopId:Long,

    val message:String?,

    val filePath: String? = null
)