package com.mits.subscription.domain.model

data class Workshop(
    val id: Long,
    var name: String,
    var subscriptions: List<Subscription>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Workshop

        if (id != other.id) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}