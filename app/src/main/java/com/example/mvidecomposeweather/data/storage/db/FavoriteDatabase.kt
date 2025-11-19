package com.example.mvidecomposeweather.data.storage.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mvidecomposeweather.data.storage.model.CityDbModel

@Database(entities = [CityDbModel::class], version = 1, exportSchema = false)
abstract class FavoriteDatabase : RoomDatabase() {

    abstract fun favoriteCitiesDao(): FavoriteCitiesDao

    companion object {
        private const val DB_NAME = "FavoriteDatabase"
        private var INSTANCE: FavoriteDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): FavoriteDatabase {
            INSTANCE?.let { return it }

            synchronized(LOCK) {
                val database = Room.databaseBuilder(
                    context = context,
                    klass = FavoriteDatabase::class.java,
                    name = DB_NAME
                ).build()

                INSTANCE = database
                return database
            }
        }
    }
}
