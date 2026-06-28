package com.cocobiz.app.di

import com.cocobiz.app.data.repository.DealerRepositoryImpl
import com.cocobiz.app.data.repository.SalesRepositoryImpl
import com.cocobiz.app.data.repository.SettingsRepositoryImpl
import com.cocobiz.app.data.repository.UserProfileRepositoryImpl
import com.cocobiz.app.domain.repository.DealerRepository
import com.cocobiz.app.domain.repository.SalesRepository
import com.cocobiz.app.domain.repository.SettingsRepository
import com.cocobiz.app.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSalesRepository(impl: SalesRepositoryImpl): SalesRepository

    @Binds
    @Singleton
    abstract fun bindDealerRepository(impl: DealerRepositoryImpl): DealerRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
