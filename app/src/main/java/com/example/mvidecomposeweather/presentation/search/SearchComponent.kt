package com.example.mvidecomposeweather.presentation.search

import com.example.mvidecomposeweather.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

interface SearchComponent {
    val state: StateFlow<SearchStore.State>
    fun changeSearchQuery(query: String)
    fun onClickBack()
    fun onClickSearch()
    fun onClickCity(city: City)
}
