package com.mits.subscription.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.model.Subscription
import kotlinx.coroutines.flow.Flow
import java.util.Date

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
    fun getById(id: Long): Flow<Subscription?>

    @Query(
        "UPDATE subscription " +
                "SET message = :mes " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateMessage(id: Long, mes: String?): Int

    @Query(
        "UPDATE subscription " +
                "SET lessonNumbers = :number " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateLessonsNumber(id: Long, number: Int): Int

    @Query(
        "UPDATE subscription " +
                "SET detail = :detail " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateDetail(id: Long, detail: String?): Int

    @Query(
        "UPDATE subscription " +
                "SET startDate = :startDate " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateStartDate(id: Long, startDate: Date): Int

    @Query(
        "UPDATE subscription " +
                "SET endDate = :endDate " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateEndDate(id: Long, endDate: Date): Int

    @Update
    suspend fun updateSubscription(subscriptionEntity: SubscriptionEntity)

}
