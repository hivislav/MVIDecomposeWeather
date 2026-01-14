package com.example.mvidecomposeweather.presentation.search

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.mvidecomposeweather.domain.entity.City
import com.example.mvidecomposeweather.domain.usecase.ChangeFavoriteStateUseCase
import com.example.mvidecomposeweather.domain.usecase.SearchCityUseCase
import com.example.mvidecomposeweather.presentation.search.SearchStore.Intent
import com.example.mvidecomposeweather.presentation.search.SearchStore.Label
import com.example.mvidecomposeweather.presentation.search.SearchStore.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

internal interface SearchStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class ChangeSearchQuery(val searchQuery: String) : Intent
        data object ClickBack : Intent
        data object ClickSearch : Intent
        data class ClickCity(val city: City) : Intent
    }

    data class State(
        val searchQuery: String,
        val searchState: SearchState
    ) {

        sealed interface SearchState {
            data object Initial : SearchState
            data object Loading : SearchState
            data object Error : SearchState
            data object EmptyResult : SearchState
            data class SuccessLoaded(val cities: List<City>) : SearchState
        }
    }

    sealed interface Label {
        data object ClickBack : Label
        data object SavedToFavorite : Label
        data class OpenForecast(val city: City) : Label
    }
}

internal class SearchStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val searchCityUseCase: SearchCityUseCase,
    private val changeFavoriteStateUseCase: ChangeFavoriteStateUseCase
) {

    fun create(openReason: OpenReason): SearchStore =
        object : SearchStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SearchStore",
            initialState = State(
                searchQuery = "",
                searchState = State.SearchState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { ExecutorImpl(openReason = openReason) },
            reducer = ReducerImpl
        ) {}

    private sealed interface Action

    private sealed interface Msg {
        data class ChangeSearchQuery(val searchQuery: String) : Msg
        data object LoadingSearchResult : Msg
        data object SearchResultError : Msg
        data class SearchResultLoaded(val cities: List<City>) : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
        }
    }

    private inner class ExecutorImpl(
        private val openReason: OpenReason
    ) : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        private var searchJob: Job? = null

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.ChangeSearchQuery -> {
                    dispatch(Msg.ChangeSearchQuery(searchQuery = intent.searchQuery))
                }

                is Intent.ClickBack -> {
                    publish(Label.ClickBack)
                }

                is Intent.ClickCity -> {
                    when (openReason) {
                        OpenReason.ADD_TO_FAVORITE -> {
                            scope.launch {
                                changeFavoriteStateUseCase.addToFavorite(city = intent.city)
                                publish(Label.SavedToFavorite)
                            }
                        }

                        OpenReason.REGULAR_SEARCH -> {
                            publish(Label.OpenForecast(city = intent.city))
                        }
                    }
                }

                is Intent.ClickSearch -> {
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        dispatch(Msg.LoadingSearchResult)
                        try {
                            val cities = searchCityUseCase(query = getState().searchQuery)
                            dispatch(Msg.SearchResultLoaded(cities = cities))
                        } catch (e: Exception) {
                            dispatch(Msg.SearchResultError)
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.ChangeSearchQuery -> {
                this.copy(searchQuery = msg.searchQuery)
            }

            is Msg.LoadingSearchResult -> {
                this.copy(searchState = State.SearchState.Loading)
            }

            is Msg.SearchResultError -> {
                this.copy(searchState = State.SearchState.Error)
            }

            is Msg.SearchResultLoaded -> {
                if (msg.cities.isEmpty()) {
                    this.copy(searchState = State.SearchState.EmptyResult)
                } else {
                    this.copy(searchState = State.SearchState.SuccessLoaded(cities = msg.cities))
                }
            }
        }
    }
}
