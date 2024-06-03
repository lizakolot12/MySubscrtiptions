package com.mits.subscription.data.db

import android.content.Context
import androidx.room.*
import com.mits.subscription.data.db.dao.WorkshopDao
import com.mits.subscription.data.db.dao.LessonDao
import com.mits.subscription.data.db.dao.SubscriptionDao
import com.mits.subscription.data.db.entity.WorkshopEntity
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity

@Database(
    entities = [WorkshopEntity::class, SubscriptionEntity::class, LessonEntity::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3)
    ]
)

@TypeConverters(DateConverter::class)
abstract class SubscriptionDb : RoomDatabase() {

    abstract fun folderDao(): WorkshopDao
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
                context,
                SubscriptionDb::class.java, "subscription.db"
            )
                .build()
    }
}