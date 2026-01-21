package com.example.mvidecomposeweather.presentation.favorite

import com.example.mvidecomposeweather.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

interface FavoriteComponent {
    val state: StateFlow<FavoriteStore.State>
    fun onClickCityItem(city: City)
    fun onClickSearch()
    fun onClickAddToFavorite()
}
