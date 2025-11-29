package com.example.mvidecomposeweather

import android.app.Application
import com.example.mvidecomposeweather.di.ApplicationComponent
import com.example.mvidecomposeweather.di.DaggerApplicationComponent

class WeatherApp : Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}
