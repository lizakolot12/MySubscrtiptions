package com.mits.subscription.data.db.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.util.*

@Entity(
    tableName = "folder"
)
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "folder_id")
    val id: Long? = null,

    val name: String?
)