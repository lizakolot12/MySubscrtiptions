package com.mits.subscription.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.data.db.model.SubscriptionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Insert
    suspend fun insert(subscriptionEntity: SubscriptionEntity): Long

    @Delete
    suspend fun delete(subscriptionEntity: SubscriptionEntity)

    @Query("DELETE FROM subscription where sub_id = :id")
    @Transaction
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM subscription where sub_id = :id")
    @Transaction
    fun getById(id: Long): Flow<SubscriptionWithDetails?>

    @Query(
        "UPDATE subscription " +
                "SET message = :mes, updatedAt = :updatedAt " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateMessage(id: Long, mes: String?, updatedAt: Long): Int

    @Query(
        "UPDATE subscription " +
                "SET lessonNumbers = :number, updatedAt = :updatedAt " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateLessonsNumber(id: Long, number: Int, updatedAt: Long): Int

    @Query(
        "UPDATE subscription " +
                "SET detail = :detail, updatedAt = :updatedAt " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateDetail(id: Long, detail: String?, updatedAt: Long): Int

    @Query(
        "UPDATE subscription " +
                "SET filePath = :uri, originFileName = :fileName, updatedAt = :updatedAt " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updatePhotoUri(id: Long, uri: String?, fileName:String?, updatedAt: Long): Int

    @Query(
        "UPDATE subscription " +
                "SET startDate = :startDate, updatedAt = :updatedAt " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateStartDate(id: Long, startDate: Long, updatedAt: Long): Int

    @Query(
        "UPDATE subscription " +
                "SET endDate = :endDate, updatedAt = :updatedAt " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateEndDate(id: Long, endDate: Long, updatedAt: Long): Int

    // Used by sync to apply server-authoritative rows: callers always pass a full entity obtained
    // via findByRemoteId and .copy(), so id/remoteId are preserved by construction. Not used for
    // local edits (those go through the targeted queries above, to avoid regenerating remoteId).
    @Update
    suspend fun updateSubscription(subscriptionEntity: SubscriptionEntity)

    @Query("SELECT filePath FROM subscription WHERE filePath IS NOT NULL")
    suspend fun getAllFilePath(): List<String>

    @Query("SELECT * FROM subscription WHERE remoteId = :remoteId LIMIT 1")
    suspend fun findByRemoteId(remoteId: String): SubscriptionEntity?

    @Query("SELECT remoteId FROM subscription WHERE sub_id = :id")
    suspend fun getRemoteId(id: Long): String?

    @Query("SELECT * FROM subscription WHERE updatedAt > :since")
    suspend fun getUpdatedSince(since: Long): List<SubscriptionEntity>

}
