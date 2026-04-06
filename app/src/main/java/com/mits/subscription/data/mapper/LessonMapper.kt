package com.mits.subscription.data.mapper

import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.domain.model.Lesson
import java.util.Date

fun LessonEntity.toDomain() = Lesson(
    lId = lId,
    description = description ?: "",
    date = date ?: Date(),
)