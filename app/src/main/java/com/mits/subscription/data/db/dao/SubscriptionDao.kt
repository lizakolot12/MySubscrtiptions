package com.mits.subscription.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.model.Subscription

@Dao
interface SubscriptionDao {

    @Insert
    suspend fun insert(subscriptionEntity: SubscriptionEntity): Long

    @Delete
    suspend fun delete(subscriptionEntity: SubscriptionEntity)

    @Query("DELETE FROM subscription where sub_id = :id")
    @Transaction
    suspend fun deleteById(id: Long)

    @Transaction
    @Query("SELECT * FROM subscription")
    fun getAll(): LiveData<List<Subscription>>

    @Query("SELECT * FROM subscription where sub_id = :id")
    @Transaction
    suspend fun getById(id: Long): Subscription

    @Query(
        "UPDATE subscription " +
                "SET message = :mes " +
                "WHERE sub_id = :id "
    )
    @Transaction
    suspend fun updateMessage(id: Long, mes: String?): Int

    @Update
    suspend fun updateSubscription(subscriptionEntity: SubscriptionEntity)

}
