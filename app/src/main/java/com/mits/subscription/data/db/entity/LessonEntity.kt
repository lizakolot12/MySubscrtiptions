package com.mits.subscription.data.db.entity

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import java.util.*

@Entity(
    tableName = "lesson",
    foreignKeys = [
        ForeignKey(
            entity = SubscriptionEntity::class,
            parentColumns = arrayOf("sub_id"),
            childColumns = arrayOf("subscription_id"),
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["remoteId"], unique = true)],
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val lId: Long,

    val description: String?,

    val date: Date?,

    @ColumnInfo(name = "subscription_id", index = true)
    val subscriptionId: Long,

    /** Stable client-generated identity used to sync this row across devices; never regenerated once set. */
    val remoteId: String = UUID.randomUUID().toString(),

    val updatedAt: Long = System.currentTimeMillis(),

    val deletedAt: Long? = null,
)
