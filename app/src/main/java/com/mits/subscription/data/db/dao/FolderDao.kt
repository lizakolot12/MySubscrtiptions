package com.mits.subscription.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mits.subscription.data.db.entity.FolderEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.model.Folder
import com.mits.subscription.model.Subscription

@Dao
interface FolderDao {

    @Insert
    suspend fun insert(folderEntity: FolderEntity): Long

    @Delete
    suspend fun delete(folderEntity: FolderEntity)

    @Transaction
    @Query("SELECT * FROM folder")
    fun getAll(): LiveData<List<Folder>>

    @Query("SELECT * FROM folder where folder_id = :id")
    @Transaction
    suspend fun getById(id: Long): Folder

    @Update
    suspend fun updateFolder(folderEntity: FolderEntity)

}
