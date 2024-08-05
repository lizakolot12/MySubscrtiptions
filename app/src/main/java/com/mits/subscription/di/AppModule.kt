package com.mits.subscription.di

import android.content.Context
import com.mits.subscription.data.db.SubscriptionDb
import com.mits.subscription.data.db.dao.LessonDao
import com.mits.subscription.data.db.dao.SubscriptionDao
import com.mits.subscription.data.db.dao.WorkshopDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): SubscriptionDb {
        return SubscriptionDb.getInstance(context)
    }

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SubscriptionRepositoryModule {

    @Provides
    fun provideLessonDao(
        database: SubscriptionDb
    ): LessonDao {
        return database.lessonDao()
    }

    @Provides
    fun provideSubscriptionDao(
        database: SubscriptionDb
    ): SubscriptionDao {
        return database.subscriptionDao()
    }
    @Provides
    fun provideWorkshopDao(
        database: SubscriptionDb
    ): WorkshopDao {
        return database.folderDao()
    }
}