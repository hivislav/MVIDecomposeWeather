package com.example.mvidecomposeweather.data.mapper

import com.example.mvidecomposeweather.data.network.dto.CityDto
import com.example.mvidecomposeweather.domain.entity.City

fun CityDto.toEntity(): City {
    return City(
        id = this.id,
        name = this.name,
        country = this.country
    )
}

fun List<CityDto>.toEntities(): List<City> {
    return this.map { it.toEntity() }
}
