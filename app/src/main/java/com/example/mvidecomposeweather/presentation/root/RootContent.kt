package com.example.mvidecomposeweather.presentation.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.example.mvidecomposeweather.presentation.details.DetailsContent
import com.example.mvidecomposeweather.presentation.favorite.FavoriteContent
import com.example.mvidecomposeweather.presentation.search.SearchContent
import com.example.mvidecomposeweather.presentation.ui.theme.MVIDecomposeWeatherTheme

@Composable
fun RootContent(component: RootComponent) {
    MVIDecomposeWeatherTheme {
        Children(stack = component.stack) {
            when (val instance = it.instance) {
                is RootComponent.Child.Details -> {
                    DetailsContent(component = instance.component)
                }

                is RootComponent.Child.Favorite -> {
                    FavoriteContent(component = instance.component)
                }

                is RootComponent.Child.Search -> {
                    SearchContent(component = instance.component)
                }
            }
        }
    }
}
