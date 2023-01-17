package com.mits.subscription.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mits.subscription.data.db.entity.WorkshopEntity
import com.mits.subscription.model.Workshop

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
    fun getAll(): LiveData<List<Workshop>>

    @Query("SELECT * FROM workshop where workshop_id = :id")
    @Transaction
    suspend fun getById(id: Long): Workshop

    @Update
    suspend fun updateWorkshop(workshopEntity: WorkshopEntity)

}
