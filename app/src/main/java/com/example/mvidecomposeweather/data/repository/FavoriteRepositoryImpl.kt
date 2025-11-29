package com.example.mvidecomposeweather.data.repository

import com.example.mvidecomposeweather.data.mapper.toDbModel
import com.example.mvidecomposeweather.data.mapper.toEntities
import com.example.mvidecomposeweather.data.storage.db.FavoriteCitiesDao
import com.example.mvidecomposeweather.domain.entity.City
import com.example.mvidecomposeweather.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private var favoriteCitiesDao: FavoriteCitiesDao
) : FavoriteRepository {
    override val favoriteCities: Flow<List<City>> = favoriteCitiesDao
        .getFavoriteCities()
        .map { it.toEntities() }

    override fun observeIsFavorite(cityId: Int): Flow<Boolean> {
        return favoriteCitiesDao.observeIsFavorite(cityId = cityId)
    }

    override suspend fun addToFavorite(city: City) {
        favoriteCitiesDao.addToFavorite(
            cityDbModel = city.toDbModel()
        )
    }

    override suspend fun removeFromFavorite(cityId: Int) {
        favoriteCitiesDao.removeFromFavorite(cityId = cityId)
    }
}
