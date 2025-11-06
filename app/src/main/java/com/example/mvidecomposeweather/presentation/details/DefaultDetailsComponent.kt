package com.example.mvidecomposeweather.presentation.details

import com.arkivanov.decompose.ComponentContext

class DefaultDetailsComponent(
    componentContext: ComponentContext
) : DetailsComponent, ComponentContext by componentContext
