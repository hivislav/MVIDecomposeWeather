package com.example.mvidecomposeweather.presentation.search

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

class DefaultSearchComponent @Inject constructor(
    private val searchStoreFactory: SearchStoreFactory,
    private val openReason: OpenReason,
    private val onClickBack: () -> Unit,
    private val onForecastForCityRequested: (City) -> Unit,
    private val onCitySavedToFavorite: () -> Unit,
    componentContext: ComponentContext
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
}
