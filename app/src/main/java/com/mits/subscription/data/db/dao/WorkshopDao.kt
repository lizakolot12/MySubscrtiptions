package com.mits.subscription.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.mits.subscription.data.db.entity.WorkshopEntity
import com.mits.subscription.data.db.model.WorkshopWithSubscriptions
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkshopDao {

    @Insert
    suspend fun insert(workshopEntity: WorkshopEntity): Long

    @Delete
    suspend fun delete(workshopEntity: WorkshopEntity)

    @Query("DELETE FROM workshop where workshop_id = :id")
    suspend fun deleteById(id:Long)

    @Transaction
    @Query("SELECT * FROM workshop")
    fun getAll(): Flow<List<WorkshopWithSubscriptions>>

    @Query("SELECT * FROM workshop where workshop_id = :id")
    @Transaction
    suspend fun getById(id: Long): WorkshopWithSubscriptions

    // Deliberately a targeted query, not @Update on the whole entity: callers build a fresh
    // WorkshopEntity with only id+name set, which would silently regenerate remoteId (breaking
    // its "stable sync identity" contract) if the whole row were replaced.
    @Query("UPDATE workshop SET name = :name, updatedAt = :updatedAt WHERE workshop_id = :id")
    suspend fun updateWorkshop(id: Long, name: String?, updatedAt: Long)

}
