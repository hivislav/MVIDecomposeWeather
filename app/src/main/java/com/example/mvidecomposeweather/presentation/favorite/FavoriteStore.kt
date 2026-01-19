package com.example.mvidecomposeweather.presentation.favorite

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.mvidecomposeweather.domain.entity.City
import com.example.mvidecomposeweather.domain.usecase.GetCurrentWeatherUseCase
import com.example.mvidecomposeweather.domain.usecase.GetFavoriteCitiesUseCase
import com.example.mvidecomposeweather.presentation.favorite.FavoriteStore.Intent
import com.example.mvidecomposeweather.presentation.favorite.FavoriteStore.Label
import com.example.mvidecomposeweather.presentation.favorite.FavoriteStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface FavoriteStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object ClickSearch : Intent
        data object ClickAddToFavorite : Intent
        data class CityItemClicked(val city: City) : Intent
    }

    data class State(
        val cityItems: List<CityItem>
    ) {
        data class CityItem(
            val city: City,
            val weatherState: WeatherState
        )

        sealed interface WeatherState {
            data object Initial : WeatherState
            data object Loading : WeatherState
            data object Error : WeatherState
            data class Loaded(
                val tempC: Float,
                val iconUrl: String
            ) : WeatherState
        }
    }

    sealed interface Label {
        data object ClickSearch : Label
        data object ClickAddToFavorite : Label
        data class CityItemClicked(val city: City) : Label
    }
}

class FavoriteStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getFavoriteCitiesUseCase: GetFavoriteCitiesUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
) {

    fun create(): FavoriteStore =
        object : FavoriteStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FavoriteStore",
            initialState = State(emptyList()),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class FavoriteCitiesLoaded(val cities: List<City>) : Action
    }

    private sealed interface Msg {

        data class FavoriteCitiesLoaded(val cities: List<City>) : Msg

        data class WeatherLoaded(
            val cityId: Int,
            val tempC: Float,
            val conditionIconUrl: String
        ) : Msg

        data class WeatherLoadingError(
            val cityId: Int
        ) : Msg

        data class WeatherIsLoading(
            val cityId: Int
        ) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                getFavoriteCitiesUseCase().collect {
                    dispatch(Action.FavoriteCitiesLoaded(cities = it))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.CityItemClicked -> publish(label = Label.CityItemClicked(city = intent.city))
                Intent.ClickSearch -> publish(label = Label.ClickSearch)
                Intent.ClickAddToFavorite -> publish(label = Label.ClickAddToFavorite)
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.FavoriteCitiesLoaded -> {
                    val cities = action.cities
                    dispatch(message = Msg.FavoriteCitiesLoaded(cities = cities))
                    cities.forEach { city ->
                        scope.launch {
                            loadWeatherForCity(cityId = city.id)
                        }
                    }
                }
            }
        }

        private suspend fun loadWeatherForCity(cityId: Int) {
            dispatch(message = Msg.WeatherIsLoading(cityId = cityId))
            try {
                val weather = getCurrentWeatherUseCase(cityId = cityId)
                dispatch(
                    message = Msg.WeatherLoaded(
                        cityId = cityId,
                        tempC = weather?.tempC ?: 0f,
                        conditionIconUrl = weather?.conditionUrl ?: ""
                    )
                )
            } catch (_: Exception) {
                dispatch(message = Msg.WeatherLoadingError(cityId = cityId))
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.FavoriteCitiesLoaded -> {
                this.copy(
                    cityItems = msg.cities.map {
                        State.CityItem(
                            city = it,
                            weatherState = State.WeatherState.Initial
                        )
                    }
                )
            }

            is Msg.WeatherIsLoading -> {
                this.copy(
                    cityItems = this.cityItems.map {
                        if (it.city.id == msg.cityId) {
                            it.copy(
                                weatherState = State.WeatherState.Loading
                            )
                        } else it
                    }
                )
            }

            is Msg.WeatherLoaded -> {
                this.copy(
                    cityItems = this.cityItems.map {
                        if (it.city.id == msg.cityId) {
                            it.copy(
                                weatherState = State.WeatherState.Loaded(
                                    tempC = msg.tempC,
                                    iconUrl = msg.conditionIconUrl
                                )
                            )
                        } else it
                    }
                )
            }

            is Msg.WeatherLoadingError -> {
                this.copy(
                    cityItems = this.cityItems.map {
                        if (it.city.id == msg.cityId) {
                            it.copy(
                                weatherState = State.WeatherState.Error
                            )
                        } else it
                    }
                )
            }
        }
    }
}
