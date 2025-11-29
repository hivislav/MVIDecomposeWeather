package com.example.mvidecomposeweather.data.repository

import com.example.mvidecomposeweather.data.mapper.toEntities
import com.example.mvidecomposeweather.data.network.api.ApiService
import com.example.mvidecomposeweather.domain.entity.City
import com.example.mvidecomposeweather.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SearchRepository {
    override suspend fun search(query: String): List<City> {
        return apiService.searchCity(query = query).toEntities()
    }
}
