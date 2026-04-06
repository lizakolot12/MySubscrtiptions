package com.mits.subscription.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.data.db.entity.WorkshopEntity

data class SubscriptionWithDetails(
    @Embedded val subscription: SubscriptionEntity,

    @Relation(
        parentColumn = "sub_id",
        entityColumn = "subscription_id",
        entity = LessonEntity::class,
    )
    val lessons: List<LessonEntity>,

    @Relation(
        parentColumn = "workshop_id",
        entityColumn = "workshop_id",
        entity = WorkshopEntity::class,
    )
    val workshop: WorkshopEntity?,
)