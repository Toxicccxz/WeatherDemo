package com.xavier.weatherdemo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.xavier.weatherdemo.data.model.Clouds
import com.xavier.weatherdemo.data.model.Coord
import com.xavier.weatherdemo.data.model.Main
import com.xavier.weatherdemo.data.model.Sys
import com.xavier.weatherdemo.data.model.Weather
import com.xavier.weatherdemo.data.model.WeatherResponse
import com.xavier.weatherdemo.data.model.Wind
import com.xavier.weatherdemo.data.repository.WeatherRepository
import com.xavier.weatherdemo.ui.viewmodel.WeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule() // This rule allows LiveData to work synchronously in tests

    private lateinit var weatherViewModel: WeatherViewModel

    @Mock
    private lateinit var weatherRepository: WeatherRepository

    @Mock
    private lateinit var observer: Observer<WeatherResponse?> // Update this to match LiveData type

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        weatherViewModel = WeatherViewModel(weatherRepository)
        weatherViewModel.weather.observeForever(observer) // Ensure observer type matches LiveData type
    }

    @Test
    fun `fetchWeather should post value to weatherData`() = runBlockingTest(testDispatcher) {
        // Given a city and mock response
        val city = "Toronto"
        val fakeWeatherResponse = createFakeWeatherResponse() // Helper method to create fake response

        // Mock repository response to return the fake weather response
        `when`(weatherRepository.getWeather(city, "your_api_key")).thenReturn(fakeWeatherResponse)

        // When fetching weather
        weatherViewModel.fetchWeather(city)

        // Then the observer should receive the value
        verify(observer).onChanged(fakeWeatherResponse)
    }

    // Helper method to create a fake WeatherResponse
    private fun createFakeWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            coord = Coord(lon = -79.3832, lat = 43.6532),  // Example coordinates for Toronto
            weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
            main = Main(
                temp = 20.5,
                feels_like = 19.0,
                temp_min = 18.0,
                temp_max = 22.0,
                pressure = 1012,
                humidity = 60,
                sea_level = null,
                grnd_level = null
            ),
            wind = Wind(speed = 5.5, deg = 220, gust = 8.0),
            clouds = Clouds(all = 0),  // No clouds in clear sky
            visibility = 10000,  // Max visibility
            timestamp = 1629474000,  // Example timestamp
            sys = Sys(type = 1, id = 1414, country = "CA", sunrise = 1629450000, sunset = 1629500000),
            timezone = -14400,  // Timezone for Toronto (-4 hours UTC)
            cityName = "Toronto",
            cod = 200
        )
    }
}