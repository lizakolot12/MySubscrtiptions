package com.mits.subscription.model

import androidx.room.ColumnInfo

open class WorkshopBase(
    @ColumnInfo(name = "workshop_id")
    open var id: Long,
    open var name: String,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkshopBase

        if (id != other.id) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}