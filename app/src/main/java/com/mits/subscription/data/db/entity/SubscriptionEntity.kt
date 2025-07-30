package com.mits.subscription.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
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
],)
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

    val filePath: String? = null
)