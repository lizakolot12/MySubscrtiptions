package com.mits.subscription.data.db

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mits.subscription.data.db.dao.FolderDao
import com.mits.subscription.data.db.dao.LessonDao
import com.mits.subscription.data.db.dao.SubscriptionDao
import com.mits.subscription.data.db.entity.FolderEntity
import com.mits.subscription.data.db.entity.LessonEntity
import com.mits.subscription.data.db.entity.SubscriptionEntity

@Database(
    entities = [FolderEntity::class, SubscriptionEntity::class, LessonEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = SubscriptionDb.AutoMigration::class
        )
    ],
    exportSchema = true
)

@TypeConverters(DateConverter::class)
abstract class SubscriptionDb : RoomDatabase() {

    abstract fun folderDao(): FolderDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun lessonDao(): LessonDao

    class AutoMigration : AutoMigrationSpec {

        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            super.onPostMigrate(db)
            db.execSQL(
                CREATE_DEFAULT_FOLDER
            )
            db.execSQL(UPDATE_EXISTING_DESCRIPTION )
        }
    }

    companion object {


        const val DEFAULT_FOLDER_ID = 1L
        val CREATE_DEFAULT_FOLDER = "INSERT INTO folder (folder_id, name)" +
                "VALUES (" + DEFAULT_FOLDER_ID + " , 'Без папки');"
        val UPDATE_EXISTING_DESCRIPTION = "UPDATE subscription \n" +
                "   SET folder_id = " + DEFAULT_FOLDER_ID

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
                .addCallback(object:Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL(CREATE_DEFAULT_FOLDER)
                    }

                })
                .build()

    }


}