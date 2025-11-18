package com.example.mvidecomposeweather.domain.usecase

import com.example.mvidecomposeweather.domain.repository.SearchRepository
import javax.inject.Inject

class SearchCityUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String) = repository.search(query)
}
