package com.example.mvidecomposeweather.presentation.favorite

import com.arkivanov.decompose.ComponentContext
import com.example.mvidecomposeweather.presentation.details.DetailsComponent

class DefaultFavoriteComponent(
    componentContext: ComponentContext
) : FavoriteComponent, ComponentContext by componentContext
