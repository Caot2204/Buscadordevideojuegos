package com.example.buscadordevideojuegos.di

import android.content.Context
import com.example.buscadordevideojuegos.data.network.VideoGameCatalogApi
import com.example.buscadordevideojuegos.data.persistance.FavoritesRepository
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VideoGameCatalagModule {
    @Provides
    @Singleton
    fun provideVideoGameCatalogApi(): VideoGameCatalogApi {
        val gson = GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .create()
        return Retrofit.Builder()
            .baseUrl("https://www.freetogame.com/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(VideoGameCatalogApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(@ApplicationContext context: Context): FavoritesRepository =
        FavoritesRepository(context)
}