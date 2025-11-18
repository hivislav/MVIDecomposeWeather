package com.example.mvidecomposeweather.domain.usecase

import com.example.mvidecomposeweather.domain.repository.FavoriteRepository
import javax.inject.Inject

class ObserveFavoriteStateUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(cityId: Int) = repository.observeIsFavorite(cityId)
}
