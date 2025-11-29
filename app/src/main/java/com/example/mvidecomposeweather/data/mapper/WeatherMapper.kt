package com.example.mvidecomposeweather.data.mapper

import com.example.mvidecomposeweather.data.network.dto.WeatherCurrentDto
import com.example.mvidecomposeweather.data.network.dto.WeatherDto
import com.example.mvidecomposeweather.data.network.dto.WeatherForecastDto
import com.example.mvidecomposeweather.domain.entity.Forecast
import com.example.mvidecomposeweather.domain.entity.Weather
import java.util.Calendar
import java.util.Date

fun WeatherCurrentDto.toEntity(): Weather? {
    return current?.toEntity()
}

fun WeatherDto.toEntity(): Weather {
    return Weather(
        tempC = this.tempC ?: 0f,
        conditionText = this.conditionDto?.text ?: "",
        conditionUrl = this.conditionDto?.iconUrl?.correctImageUrl() ?: "",
        date = this.date?.toCalendar() ?: Calendar.getInstance()
    )
}

fun WeatherForecastDto.toEntity(): Forecast {
    return Forecast(
        currentWeather = this.current.toEntity(),
        upcoming = this.forecastDto?.forecastDay
            // т.к. в прогнозе прилетает 4 дня, 1 из которых текущий. А нам нужен только прогноз
            ?.drop(1)
            ?.map { dayDto ->
                val dayWeatherDto = dayDto.dayWeatherDto
                Weather(
                    tempC = dayWeatherDto?.tempC ?: 0f,
                    conditionText = dayWeatherDto?.conditionDto?.text ?: "",
                    conditionUrl = dayWeatherDto?.conditionDto?.iconUrl?.correctImageUrl() ?: "",
                    date = dayDto.date?.toCalendar() ?: Calendar.getInstance()
                )
            } ?: emptyList()
    )
}

private fun Long.toCalendar(): Calendar {
    return Calendar.getInstance().apply {
        time = Date(this@toCalendar * 1000)
    }
}

private fun String.correctImageUrl(): String {
    return "https:$this".replace(
        oldValue = "64x64",
        newValue = "128x128"
    )
}
