package com.joaquin.uala.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val country: String,
    val lon: Double,
    val lat: Double,
    @ColumnInfo(defaultValue = "0")
    val isFavorite: Boolean = false
)