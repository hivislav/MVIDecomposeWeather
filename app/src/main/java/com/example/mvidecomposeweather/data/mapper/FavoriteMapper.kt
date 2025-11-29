package com.example.mvidecomposeweather.data.mapper

import com.example.mvidecomposeweather.data.storage.model.CityDbModel
import com.example.mvidecomposeweather.domain.entity.City

fun CityDbModel.toEntity(): City {
    return City(
        id = this.id,
        name = this.name,
        country = this.country
    )
}

fun List<CityDbModel>.toEntities(): List<City> {
    return this.map { it.toEntity() }
}

fun City.toDbModel(): CityDbModel {
    return CityDbModel(
        id = this.id,
        name = this.name,
        country = this.country
    )
}
