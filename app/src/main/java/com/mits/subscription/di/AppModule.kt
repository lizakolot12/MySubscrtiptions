package com.mits.subscription.di

import android.content.Context
import com.mits.subscription.data.db.SubscriptionDb
import com.mits.subscription.data.repo.SubscriptionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): SubscriptionDb {
        return SubscriptionDb.getInstance(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SubscriptionRepositoryModule {

    @Singleton
    @Provides
    fun provideSubscriptionRepository(
        database: SubscriptionDb
    ): SubscriptionRepository {
        return SubscriptionRepository(
            database.lessonDao(), database.subscriptionDao(), database.folderDao()
        )
    }
}