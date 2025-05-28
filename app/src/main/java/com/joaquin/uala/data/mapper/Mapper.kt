package com.joaquin.uala.data.mapper

import com.joaquin.uala.data.local.entity.CityEntity
import com.joaquin.uala.data.remoto.response.CityResponse
import com.joaquin.uala.domain.model.CityModel

fun CityResponse.toModel(): CityModel {
    return CityModel(
        id = id,
        name = name,
        country = country,
        lat = coord.lat,
        lon = coord.lon,
        isFavorite = false
    )
}

fun CityModel.toEntity(): CityEntity {
    return CityEntity(
        id = id,
        name = name,
        country = country,
        lat = lat,
        lon = lon,
        isFavorite = isFavorite
    )
}

fun CityEntity.toModel(): CityModel {
    return CityModel(
        id = id,
        name = name,
        country = country,
        lat = lat,
        lon = lon,
        isFavorite = isFavorite
    )
}


