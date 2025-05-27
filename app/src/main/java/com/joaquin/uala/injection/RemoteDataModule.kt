package com.joaquin.uala.injection

import com.joaquin.uala.data.remoto.CitiesRemoteDataSource
import com.joaquin.uala.data.remoto.CitiesRemoteDataSourceImpl
import com.joaquin.uala.data.remoto.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataModule {

    @Provides
    @Singleton
    fun provideMovieRemoteDataSource(
        apiService: ApiService
    ): CitiesRemoteDataSource {
        return CitiesRemoteDataSourceImpl(apiService)
    }
}