package com.mits.subscription.model

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity
import java.util.*

data class Folder(
    @ColumnInfo(name = "folder_id")
    var id: Long,
    var name: String,

    @Relation(
        parentColumn = "folder_id",
        entityColumn = "folder_id",
        entity = SubscriptionEntity::class
    )
    var subscriptions: List<Subscription>? = null
)