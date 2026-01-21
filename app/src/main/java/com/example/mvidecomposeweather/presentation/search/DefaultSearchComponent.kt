package com.example.mvidecomposeweather.presentation.search

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

class DefaultSearchComponent @AssistedInject constructor(
    private val searchStoreFactory: SearchStoreFactory,
    @Assisted("openReason") private val openReason: OpenReason,
    @Assisted("onClickBack") private val onClickBack: () -> Unit,
    @Assisted("onForecastForCityRequested") private val onForecastForCityRequested: (City) -> Unit,
    @Assisted("onCitySavedToFavorite") private val onCitySavedToFavorite: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : SearchComponent,
    ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        searchStoreFactory.create(openReason = openReason)
    }

    private val scope = componentScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<SearchStore.State> = store.stateFlow

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    is SearchStore.Label.ClickBack -> {
                        onClickBack()
                    }

                    is SearchStore.Label.OpenForecast -> {
                        onForecastForCityRequested(it.city)
                    }

                    is SearchStore.Label.SavedToFavorite -> {
                        onCitySavedToFavorite()
                    }
                }
            }
        }
    }

    override fun changeSearchQuery(query: String) {
        store.accept(SearchStore.Intent.ChangeSearchQuery(searchQuery = query))
    }

    override fun onClickBack() {
        store.accept(SearchStore.Intent.ClickBack)
    }

    override fun onClickSearch() {
        store.accept(SearchStore.Intent.ClickSearch)
    }

    override fun onClickCity(city: City) {
        store.accept(SearchStore.Intent.ClickCity(city = city))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("openReason") openReason: OpenReason,
            @Assisted("onClickBack") onClickBack: () -> Unit,
            @Assisted("onForecastForCityRequested") onForecastForCityRequested: (City) -> Unit,
            @Assisted("onCitySavedToFavorite") onCitySavedToFavorite: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultSearchComponent
    }
}
