package com.mits.subscription.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mits.subscription.data.db.dao.LessonDao
import com.mits.subscription.data.db.dao.SubscriptionDao
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity

@Database(entities = [ SubscriptionEntity::class, LessonEntity::class,], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class SubscriptionDb : RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun lessonDao(): LessonDao

    companion object {

        @Volatile
        private var INSTANCE: SubscriptionDb? = null

        fun getInstance(context: Context): SubscriptionDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                SubscriptionDb::class.java, "subscription.db"
            )
                .build()
    }
}