package com.example.mvidecomposeweather.presentation.details

import kotlinx.coroutines.flow.StateFlow

interface DetailsComponent {
    val state: StateFlow<DetailsStore.State>
    fun onClickBack()
    fun onClickChangeFavoriteStatus()
}
