package com.mits.subscription.domain.model

data class WorkshopInfo(
    val id: Long,
    val name: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkshopInfo

        if (id != other.id) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}