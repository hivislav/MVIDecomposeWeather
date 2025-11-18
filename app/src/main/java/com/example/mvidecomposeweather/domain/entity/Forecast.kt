package com.example.mvidecomposeweather.domain.entity

data class Forecast(
    val currentWeather: Weather,
    val upcoming: List<Weather>
)
