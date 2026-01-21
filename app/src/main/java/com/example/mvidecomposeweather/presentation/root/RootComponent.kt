package com.example.mvidecomposeweather.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.mvidecomposeweather.presentation.details.DetailsComponent
import com.example.mvidecomposeweather.presentation.favorite.FavoriteComponent
import com.example.mvidecomposeweather.presentation.search.SearchComponent

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Favorite(val component: FavoriteComponent) : Child
        data class Search(val component: SearchComponent) : Child
        data class Details(val component: DetailsComponent) : Child
    }
}
