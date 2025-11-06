package com.example.mvidecomposeweather.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.example.mvidecomposeweather.presentation.details.DetailsComponent
import com.example.mvidecomposeweather.presentation.favorite.FavoriteComponent

class DefaultRootComponent(
    componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext
