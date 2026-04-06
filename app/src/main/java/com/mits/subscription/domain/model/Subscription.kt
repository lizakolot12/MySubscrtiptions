package com.mits.subscription.domain.model

data class Subscription(
    val id: Long,
    val detail: String? = null,
    val startDate: Long?,
    val endDate: Long?,
    val lessonNumbers: Int = 0,
    val lessons: List<Lesson>? = null,
    val workshop: WorkshopInfo? = null,
    val workshopId: Long,
    val message: String?,
    val filePath: String? = null,
    val originFileName: String? = null,
)