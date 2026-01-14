package com.example.mvidecomposeweather.presentation.details

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.mvidecomposeweather.domain.entity.City
import com.example.mvidecomposeweather.domain.entity.Forecast
import com.example.mvidecomposeweather.domain.usecase.ChangeFavoriteStateUseCase
import com.example.mvidecomposeweather.domain.usecase.GetForecastUseCase
import com.example.mvidecomposeweather.domain.usecase.ObserveFavoriteStateUseCase
import com.example.mvidecomposeweather.presentation.details.DetailsStore.Intent
import com.example.mvidecomposeweather.presentation.details.DetailsStore.Label
import com.example.mvidecomposeweather.presentation.details.DetailsStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

internal interface DetailsStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object ClickBack : Intent
        data object ClickChangeFavoriteStatus : Intent
    }

    data class State(
        val city: City,
        val isFavorite: Boolean,
        val forecastState: ForecastState
    ) {
        sealed interface ForecastState {
            data object Initial : ForecastState
            data object Loading : ForecastState
            data object Error : ForecastState
            data class Loaded(val forecast: Forecast) : ForecastState
        }
    }

    sealed interface Label {
        data object ClickBack : Label
    }
}

internal class DetailsStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getForecastUseCase: GetForecastUseCase,
    private val changeFavoriteStateUseCase: ChangeFavoriteStateUseCase,
    private val observeFavoriteStateUseCase: ObserveFavoriteStateUseCase
) {

    fun create(city: City): DetailsStore =
        object : DetailsStore, Store<Intent, State, Label> by storeFactory.create(
            name = "DetailsStore",
            initialState = State(
                city = city,
                isFavorite = false,
                forecastState = State.ForecastState.Initial
            ),
            bootstrapper = BootstrapperImpl(city = city),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class FavoriteStatusChanged(val isFavorite: Boolean) : Action
        data class ForecastLoaded(val forecast: Forecast) : Action
        data object ForecastStartLoading : Action
        data object ForecastLoadingError : Action
    }

    private sealed interface Msg {
        data class FavoriteStatusChanged(val isFavorite: Boolean) : Msg
        data class ForecastLoaded(val forecast: Forecast) : Msg
        data object ForecastStartLoading : Msg
        data object ForecastLoadingError : Msg
    }

    private inner class BootstrapperImpl(
        private val city: City
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                observeFavoriteStateUseCase(cityId = city.id).collect {
                    dispatch(Action.FavoriteStatusChanged(isFavorite = it))
                }
            }
            scope.launch {
                dispatch(Action.ForecastStartLoading)
                try {
                    val forecast = getForecastUseCase(cityId = city.id)
                    dispatch(Action.ForecastLoaded(forecast = forecast))
                } catch (e: Exception) {
                    dispatch(Action.ForecastLoadingError)
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.ClickBack -> {
                    publish(Label.ClickBack)
                }

                Intent.ClickChangeFavoriteStatus -> {
                    val state = getState()
                    if (state.isFavorite) {
                        scope.launch {
                            changeFavoriteStateUseCase.removeFromFavorite(state.city.id)
                        }
                    } else {
                        scope.launch {
                            changeFavoriteStateUseCase.addToFavorite(city = state.city)
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.FavoriteStatusChanged -> {
                    dispatch(Msg.FavoriteStatusChanged(isFavorite = action.isFavorite))
                }

                is Action.ForecastLoaded -> {
                    dispatch(Msg.ForecastLoaded(forecast = action.forecast))
                }

                is Action.ForecastLoadingError -> {
                    dispatch(Msg.ForecastLoadingError)
                }

                is Action.ForecastStartLoading -> {
                    dispatch(Msg.ForecastStartLoading)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.FavoriteStatusChanged -> {
                this.copy(isFavorite = msg.isFavorite)
            }

            is Msg.ForecastLoaded -> {
                this.copy(
                    forecastState = State.ForecastState.Loaded(
                        forecast = msg.forecast
                    )
                )
            }

            is Msg.ForecastLoadingError -> {
                this.copy(
                    forecastState = State.ForecastState.Error
                )
            }

            is Msg.ForecastStartLoading -> {
                this.copy(
                    forecastState = State.ForecastState.Loading
                )
            }
        }
    }
}
