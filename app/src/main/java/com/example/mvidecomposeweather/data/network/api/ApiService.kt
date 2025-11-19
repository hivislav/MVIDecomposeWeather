package com.example.mvidecomposeweather.data.network.api

import com.example.mvidecomposeweather.data.network.dto.CityDto
import com.example.mvidecomposeweather.data.network.dto.WeatherCurrentDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current.json?")
    suspend fun loadCurrentWeather(
        @Query("q") query: String
    ): WeatherCurrentDto

    @GET("forecast.json?")
    suspend fun loadForecast(
        @Query("q") query: String,
        @Query("days") daysCount: Int = 4
    ): WeatherCurrentDto

    @GET("search.json?")
    suspend fun searchCity(
        @Query("q") query: String,
    ): List<CityDto>
}
