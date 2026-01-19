package com.example.mvidecomposeweather.presentation.details

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

class DefaultDetailsComponent @Inject constructor(
    private val detailsStoreFactory: DetailsStoreFactory,
    private val city: City,
    private val onClickBack: () -> Unit,
    componentContext: ComponentContext
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
                    DetailsStore.Label.ClickBack -> onClickBack()
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
}
