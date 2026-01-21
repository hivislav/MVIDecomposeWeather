package com.example.mvidecomposeweather.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.example.mvidecomposeweather.WeatherApp
import com.example.mvidecomposeweather.presentation.root.DefaultRootComponent
import com.example.mvidecomposeweather.presentation.root.RootContent
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootComponentFactory: DefaultRootComponent.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as WeatherApp).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val rootComponent = rootComponentFactory.create(defaultComponentContext())
        setContent {
            RootContent(component = rootComponent)
        }
    }
}
