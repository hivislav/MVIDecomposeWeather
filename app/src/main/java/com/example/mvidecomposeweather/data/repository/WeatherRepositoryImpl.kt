package com.example.mvidecomposeweather.data.repository

import com.example.mvidecomposeweather.data.mapper.toEntity
import com.example.mvidecomposeweather.data.network.api.ApiService
import com.example.mvidecomposeweather.domain.entity.Forecast
import com.example.mvidecomposeweather.domain.entity.Weather
import com.example.mvidecomposeweather.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : WeatherRepository {
    override suspend fun getWeather(cityId: Int): Weather? {
        return apiService.loadCurrentWeather(query = "$PREFIX_CITY_ID$cityId").toEntity()
    }

    override suspend fun getForecast(cityId: Int): Forecast {
        return apiService.loadForecast(query = "$PREFIX_CITY_ID$cityId").toEntity()
    }

    companion object {
        private const val PREFIX_CITY_ID = "id:"
    }
}
