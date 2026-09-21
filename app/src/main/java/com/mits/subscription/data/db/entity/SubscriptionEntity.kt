package com.mits.subscription.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "subscription",
        foreignKeys = [
        androidx.room.ForeignKey(
            entity = WorkshopEntity::class,
            parentColumns = arrayOf("workshop_id"),
            childColumns = arrayOf("workshop_id"),
            onDelete = androidx.room.ForeignKey.CASCADE
        )
],
        indices = [Index(value = ["remoteId"], unique = true)],
)
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="sub_id")
    val id: Long? = null,

    val detail: String?,

    val startDate: Long?,

    val endDate: Long?,

    val lessonNumbers: Int,

    @ColumnInfo(name = "workshop_id", index = true)
    val workshopId: Long,

    val message:String?,

    val filePath: String? = null,

    val originFileName: String? = null,

    /** Stable client-generated identity used to sync this row across devices; never regenerated once set. */
    val remoteId: String = UUID.randomUUID().toString(),

    val updatedAt: Long = System.currentTimeMillis(),

    val deletedAt: Long? = null,
)
