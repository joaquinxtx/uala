package com.joaquin.uala.injection

import com.joaquin.uala.data.local.CitiesLocalDataSource
import com.joaquin.uala.data.local.CitiesLocalDataSourceImpl
import com.joaquin.uala.data.local.dao.LocationFavDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {

    @Provides
    @Singleton
    fun provideMovieLocalDataSource(
        dao: LocationFavDao
    ): CitiesLocalDataSource {
        return CitiesLocalDataSourceImpl(dao)
    }
}