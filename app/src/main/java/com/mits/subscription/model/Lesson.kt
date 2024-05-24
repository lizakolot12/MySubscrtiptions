package com.mits.subscription.model

import java.util.*

data class Lesson(
    var lId: Long,
    var description: String,
    var date:Date,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Lesson) return false

        if (lId != other.lId) return false
        if (description != other.description) return false
        return date == other.date
    }

    override fun hashCode(): Int {
        var result = lId.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}