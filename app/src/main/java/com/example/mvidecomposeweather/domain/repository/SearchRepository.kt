package com.example.mvidecomposeweather.domain.repository

import com.example.mvidecomposeweather.domain.entity.City

interface SearchRepository {
    suspend fun search(query: String): List<City>
}
