package com.mits.subscription.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.data.db.entity.WorkshopEntity

data class WorkshopWithSubscriptions(
    @Embedded val workshop: WorkshopEntity,

    @Relation(
        parentColumn = "workshop_id",
        entityColumn = "workshop_id",
        entity = SubscriptionEntity::class,
    )
    val subscriptions: List<SubscriptionWithDetails>,
)