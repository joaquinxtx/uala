package com.joaquin.uala.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joaquin.uala.data.local.dao.LocationFavDao
import com.joaquin.uala.data.local.entity.CityEntity

@Database(entities = [CityEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationFavDao(): LocationFavDao
}