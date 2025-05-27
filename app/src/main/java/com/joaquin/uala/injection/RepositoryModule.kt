package com.joaquin.uala.injection

import com.joaquin.uala.data.local.CitiesLocalDataSource
import com.joaquin.uala.data.remoto.CitiesRemoteDataSource
import com.joaquin.uala.data.repositoriesImpl.CityRepositoryImpl
import com.joaquin.uala.domain.repository.CityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieRepository(
        remoteDataSource: CitiesRemoteDataSource,
        localDataSource: CitiesLocalDataSource
    ): CityRepository{
        return CityRepositoryImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource
        )
    }
}