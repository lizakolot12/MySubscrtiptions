package com.mits.subscription.data.db.entity

import androidx.room.*
import java.util.UUID

@Entity(
    tableName = "workshop",
    indices = [Index(value = ["remoteId"], unique = true)],
)
data class WorkshopEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "workshop_id")
    val id: Long? = null,

    val name: String?,

    /** Stable client-generated identity used to sync this row across devices; never regenerated once set. */
    val remoteId: String = UUID.randomUUID().toString(),

    val updatedAt: Long = System.currentTimeMillis(),

    val deletedAt: Long? = null,
)
