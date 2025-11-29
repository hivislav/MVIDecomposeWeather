package com.example.mvidecomposeweather.data.storage.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class CityDbModel(
    @PrimaryKey
    val id: Int,
    val name: String,
    val country: String
)
