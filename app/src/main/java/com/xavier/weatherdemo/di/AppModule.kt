package com.xavier.weatherdemo.di

import com.xavier.weatherdemo.data.api.WeatherApi
import com.xavier.weatherdemo.data.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi): WeatherRepository = WeatherRepository(api)
}