package com.mits.subscription.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mits.subscription.data.db.entity.WorkshopEntity
import com.mits.subscription.model.Workshop
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
    fun getAll(): Flow<List<Workshop>>

    @Query("SELECT * FROM workshop where workshop_id = :id")
    @Transaction
    suspend fun getById(id: Long): Workshop

    @Update
    suspend fun updateWorkshop(workshopEntity: WorkshopEntity)

}
