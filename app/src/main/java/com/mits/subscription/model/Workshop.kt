package com.mits.subscription.model

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.mits.subscription.data.db.entity.SubscriptionEntity

data class Workshop(
    @ColumnInfo(name = "workshop_id")
    var id: Long,
    var name: String,

    @Relation(
        parentColumn = "workshop_id",
        entityColumn = "workshop_id",
        entity = SubscriptionEntity::class
    )
    var subscriptions: List<Subscription>? = null
)