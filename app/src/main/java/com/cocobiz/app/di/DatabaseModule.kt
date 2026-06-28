package com.cocobiz.app.di

import android.content.Context
import androidx.room.Room
import com.cocobiz.app.data.local.dao.DealerDao
import com.cocobiz.app.data.local.dao.SalesEntryDao
import com.cocobiz.app.data.local.dao.SettingsDao
import com.cocobiz.app.data.local.dao.UserProfileDao
import com.cocobiz.app.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserProfileDao(db: AppDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    fun provideDealerDao(db: AppDatabase): DealerDao = db.dealerDao()

    @Provides
    fun provideSalesEntryDao(db: AppDatabase): SalesEntryDao = db.salesEntryDao()

    @Provides
    fun provideSettingsDao(db: AppDatabase): SettingsDao = db.settingsDao()
}
