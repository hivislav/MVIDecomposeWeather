package com.example.mvidecomposeweather.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.example.mvidecomposeweather.domain.entity.City
import com.example.mvidecomposeweather.presentation.details.DefaultDetailsComponent
import com.example.mvidecomposeweather.presentation.favorite.DefaultFavoriteComponent
import com.example.mvidecomposeweather.presentation.search.DefaultSearchComponent
import com.example.mvidecomposeweather.presentation.search.OpenReason
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

class DefaultRootComponent @AssistedInject constructor(
    private val detailsComponentFactory: DefaultDetailsComponent.Factory,
    private val favoriteComponentFactory: DefaultFavoriteComponent.Factory,
    private val searchComponentFactory: DefaultSearchComponent.Factory,
    @Assisted("componentContext") componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Favorite,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            is Config.Details -> {
                val component = detailsComponentFactory.create(
                    city = config.city,
                    onClickBack = {
                        navigation.pop()
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Details(component = component)
            }

            is Config.Favorite -> {
                val component = favoriteComponentFactory.create(
                    onCityItemClick = {
                        navigation.push(Config.Details(city = it))
                    },
                    onSearchClick = {
                        navigation.push(Config.Search(openReason = OpenReason.REGULAR_SEARCH))
                    },
                    onAddFavoriteClick = {
                        navigation.push(Config.Search(openReason = OpenReason.ADD_TO_FAVORITE))
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Favorite(component = component)
            }

            is Config.Search -> {
                val component = searchComponentFactory.create(
                    openReason = config.openReason,
                    onClickBack = {
                        navigation.pop()
                    },
                    onForecastForCityRequested = {
                        navigation.push(Config.Details(city = it))
                    },
                    onCitySavedToFavorite = {
                        navigation.pop()
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Search(component = component)
            }
        }
    }

    @Serializable
    private sealed interface Config {

        @Serializable
        data object Favorite : Config

        @Serializable
        data class Search(val openReason: OpenReason) : Config

        @Serializable
        data class Details(val city: City) : Config
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultRootComponent
    }
}
