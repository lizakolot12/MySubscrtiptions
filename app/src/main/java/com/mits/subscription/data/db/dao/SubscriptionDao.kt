package com.mits.subscription.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mits.subscription.data.db.entity.SubscriptionEntity
import com.mits.subscription.model.Subscription

@Dao
interface SubscriptionDao {

    @Insert
    suspend fun insert(subscriptionEntity: SubscriptionEntity):Long

    @Delete
    suspend fun delete(subscriptionEntity: SubscriptionEntity)

    @Transaction
    @Query("SELECT * FROM subscription")
    fun getAll(): LiveData<List<Subscription>>

    @Query("SELECT * FROM subscription where sub_id = :id")
    @Transaction
    suspend fun getById(id: Long): Subscription

    @Update
    suspend fun updateSubscription(subscriptionEntity: SubscriptionEntity)
}
