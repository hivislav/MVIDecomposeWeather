package com.example.mvidecomposeweather.di

import android.content.Context
import com.example.mvidecomposeweather.data.network.api.ApiFactory
import com.example.mvidecomposeweather.data.network.api.ApiService
import com.example.mvidecomposeweather.data.repository.FavoriteRepositoryImpl
import com.example.mvidecomposeweather.data.repository.SearchRepositoryImpl
import com.example.mvidecomposeweather.data.repository.WeatherRepositoryImpl
import com.example.mvidecomposeweather.data.storage.db.FavoriteCitiesDao
import com.example.mvidecomposeweather.data.storage.db.FavoriteDatabase
import com.example.mvidecomposeweather.domain.repository.FavoriteRepository
import com.example.mvidecomposeweather.domain.repository.SearchRepository
import com.example.mvidecomposeweather.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @ApplicationScope
    @Binds
    fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @ApplicationScope
    @Binds
    fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    companion object {

        @ApplicationScope
        @Provides
        fun provideApiService() : ApiService {
            return ApiFactory.apiService
        }

        @ApplicationScope
        @Provides
        fun provideFavoriteDatabase(context: Context) : FavoriteDatabase {
            return FavoriteDatabase.getInstance(context)
        }

        @ApplicationScope
        @Provides
        fun provideFavoriteCitiesDao(database: FavoriteDatabase) : FavoriteCitiesDao {
            return database.favoriteCitiesDao()
        }
    }
}
