package com.example.mvidecomposeweather.domain.usecase

import com.example.mvidecomposeweather.domain.entity.City
import com.example.mvidecomposeweather.domain.repository.FavoriteRepository
import javax.inject.Inject

class ChangeFavoriteStateUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend fun addToFavorite(city: City) = repository.addToFavorite(city)

    suspend fun removeFromFavorite(cityId: Int) = repository.removeFromFavorite(cityId)
}
