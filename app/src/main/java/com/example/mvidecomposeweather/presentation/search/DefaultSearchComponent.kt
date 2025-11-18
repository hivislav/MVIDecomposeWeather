package com.example.mvidecomposeweather.presentation.search

import com.arkivanov.decompose.ComponentContext
import com.example.mvidecomposeweather.presentation.details.DetailsComponent
import com.example.mvidecomposeweather.presentation.favorite.FavoriteComponent

class DefaultSearchComponent(
    componentContext: ComponentContext
) : SearchComponent, ComponentContext by componentContext
