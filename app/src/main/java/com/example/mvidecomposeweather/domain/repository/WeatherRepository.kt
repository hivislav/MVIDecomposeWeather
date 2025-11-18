package com.example.mvidecomposeweather.domain.repository

import com.example.mvidecomposeweather.domain.entity.Forecast
import com.example.mvidecomposeweather.domain.entity.Weather

interface WeatherRepository {
    suspend fun getWeather(cityId: Int): Weather
    suspend fun getForecast(cityId: Int): Forecast
}
