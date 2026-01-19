package com.example.mvidecomposeweather.presentation.favorite

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.mvidecomposeweather.domain.entity.City
import com.example.mvidecomposeweather.presentation.extensions.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultFavoriteComponent @Inject constructor(
    private val favoriteStoreFactory: FavoriteStoreFactory,
    private val onCityItemClick: (City) -> Unit,
    private val onSearchClick: () -> Unit,
    private val onAddFavoriteClick: () -> Unit,
    componentContext: ComponentContext
) : FavoriteComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { favoriteStoreFactory.create() }
    private val scope = componentScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<FavoriteStore.State> = store.stateFlow

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    is FavoriteStore.Label.CityItemClicked -> {
                        onCityItemClick(it.city)
                    }

                    is FavoriteStore.Label.ClickSearch -> {
                        onSearchClick()
                    }

                    is FavoriteStore.Label.ClickAddToFavorite -> {
                        onAddFavoriteClick()
                    }
                }
            }
        }
    }

    override fun onCityItemClick(city: City) {
        store.accept(FavoriteStore.Intent.CityItemClicked(city = city))
    }

    override fun onClickSearch() {
        store.accept(FavoriteStore.Intent.ClickSearch)
    }

    override fun onClickAddToFavorite() {
        store.accept(FavoriteStore.Intent.ClickAddToFavorite)
    }
}
