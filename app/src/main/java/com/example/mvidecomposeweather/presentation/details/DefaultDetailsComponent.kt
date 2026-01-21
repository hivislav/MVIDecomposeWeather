package com.example.mvidecomposeweather.presentation.details

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

class DefaultDetailsComponent @AssistedInject constructor(
    private val detailsStoreFactory: DetailsStoreFactory,
    @Assisted("city") private val city: City,
    @Assisted("onBackClick") private val onBackClick: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : DetailsComponent,
    ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { detailsStoreFactory.create(city = city) }
    private val scope = componentScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<DetailsStore.State> = store.stateFlow

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    DetailsStore.Label.ClickBack -> onBackClick()
                }
            }
        }
    }

    override fun onClickBack() {
        store.accept(DetailsStore.Intent.ClickBack)
    }

    override fun onClickChangeFavoriteStatus() {
        store.accept(DetailsStore.Intent.ClickChangeFavoriteStatus)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("city") city: City,
            @Assisted("onBackClick") onBackClick: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultDetailsComponent
    }
}
