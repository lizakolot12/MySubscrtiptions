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
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val lId: Long,

    val description: String?,

    val date: Date?,

    @ColumnInfo(name = "subscription_id", index = true)
    val subscriptionId: Long
)