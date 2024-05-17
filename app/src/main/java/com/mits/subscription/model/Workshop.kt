package com.mits.subscription.model

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.mits.subscription.data.db.entity.SubscriptionEntity

class Workshop(
    @ColumnInfo(name = "workshop_id")
    override var id: Long,
    override var name: String,

    @Relation(
        parentColumn = "workshop_id",
        entityColumn = "workshop_id",
        entity = SubscriptionEntity::class
    )
    var subscriptions: List<Subscription>? = null
):WorkshopBase(id, name)