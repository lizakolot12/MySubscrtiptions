package com.mits.subscription.model

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.mits.subscription.data.db.SubscriptionDb
import com.mits.subscription.data.db.entity.LessonEntity
import java.util.*

data class Subscription(

    @ColumnInfo(name="sub_id")
    var id: Long,
    var name: String,
    var startDate: Date?,
    var endDate: Date?,
    var lessonNumbers: Int = 0,
    var description: String?,

    @Relation(
        parentColumn = "sub_id",
        entityColumn = "subscription_id",
        entity = LessonEntity::class
    )
    var lessons: List<Lesson>? = null,

    @ColumnInfo(name="folder_id")
    var folderId:Long? = SubscriptionDb.DEFAULT_FOLDER_ID
)