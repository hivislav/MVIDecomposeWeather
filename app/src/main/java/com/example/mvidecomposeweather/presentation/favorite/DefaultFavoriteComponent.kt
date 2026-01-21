package com.example.mvidecomposeweather.presentation.favorite

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.mvidecomposeweather.domain.entity.City
import com.example.mvidecomposeweather.presentation.extensions.componentScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultFavoriteComponent @AssistedInject constructor(
    private val favoriteStoreFactory: FavoriteStoreFactory,
    @Assisted("onCityItemClick") private val onCityItemClick: (City) -> Unit,
    @Assisted("onSearchClick") private val onSearchClick: () -> Unit,
    @Assisted("onAddFavoriteClick") private val onAddFavoriteClick: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
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

    override fun onClickCityItem(city: City) {
        store.accept(FavoriteStore.Intent.CityItemClicked(city = city))
    }

    override fun onClickSearch() {
        store.accept(FavoriteStore.Intent.ClickSearch)
    }

    override fun onClickAddToFavorite() {
        store.accept(FavoriteStore.Intent.ClickAddToFavorite)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onCityItemClick") onCityItemClick: (City) -> Unit,
            @Assisted("onSearchClick") onSearchClick: () -> Unit,
            @Assisted("onAddFavoriteClick") onAddFavoriteClick: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultFavoriteComponent
    }
}
