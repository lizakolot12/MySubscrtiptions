package com.mits.subscription.data.db.entity

import androidx.room.*

@Entity(
    tableName = "workshop"
)
data class WorkshopEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "workshop_id")
    val id: Long? = null,

    val name: String?
)