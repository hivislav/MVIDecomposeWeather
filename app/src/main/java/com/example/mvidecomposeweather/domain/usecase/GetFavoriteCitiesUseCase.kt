package com.example.mvidecomposeweather.domain.usecase

import com.example.mvidecomposeweather.domain.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoriteCitiesUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke() = repository.favoriteCities
}
