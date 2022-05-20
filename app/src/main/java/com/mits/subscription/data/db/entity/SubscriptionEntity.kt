package com.mits.subscription.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "subscription")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="sub_id")
    val id: Long,

    val name: String,

    val startDate: Date?,

    val endDate: Date?,

    val lessonNumbers: Int,

    val description: String?,
)