package com.joaquin.uala.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joaquin.uala.data.local.entity.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationFavDao {
    @Query("SELECT * FROM cities")
    fun getFavoriteCities(): Flow<List<CityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CityEntity)

    @Delete
    suspend fun delete(city: CityEntity)

    @Query("DELETE FROM cities WHERE id = :id")
    suspend fun deleteById(id: Long)
}