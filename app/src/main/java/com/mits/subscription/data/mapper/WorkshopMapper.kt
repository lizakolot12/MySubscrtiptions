package com.mits.subscription.data.mapper

import com.mits.subscription.data.db.model.WorkshopWithSubscriptions
import com.mits.subscription.domain.model.Workshop

fun WorkshopWithSubscriptions.toDomain() = Workshop(
    id = workshop.id ?: 0L,
    name = workshop.name ?: "",
    subscriptions = subscriptions.filter { it.subscription.deletedAt == null }.map { it.toDomain() },
)