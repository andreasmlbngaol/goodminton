package com.mightsana.goodminton.model.di

import com.mightsana.goodminton.model.repository.AppRepository
import com.mightsana.goodminton.model.repository.AppRepositoryImpl
import com.mightsana.goodminton.model.service.AccountService
import com.mightsana.goodminton.model.service.FirebaseAccountService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAccountService(): AccountService = FirebaseAccountService()

    @Provides
    @Singleton
    fun provideOneRepository(): AppRepository = AppRepositoryImpl()
}