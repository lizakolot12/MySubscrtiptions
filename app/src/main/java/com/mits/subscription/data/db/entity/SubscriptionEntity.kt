package com.mits.subscription.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mits.subscription.data.db.SubscriptionDb
import java.util.*

@Entity(tableName = "subscription",
        foreignKeys = [
        androidx.room.ForeignKey(
            entity = FolderEntity::class,
            parentColumns = arrayOf("folder_id"),
            childColumns = arrayOf("folder_id"),
            onDelete = androidx.room.ForeignKey.CASCADE
        )
],)
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="sub_id")
    val id: Long? = null,

    val name: String?,

    val startDate: Date?,

    val endDate: Date?,

    val lessonNumbers: Int,

    @ColumnInfo(name = "folder_id", index = true)
    val folderId: Long
)