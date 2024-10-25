package com.xavier.weatherdemo

import com.xavier.weatherdemo.data.api.WeatherApi
import com.xavier.weatherdemo.data.model.Clouds
import com.xavier.weatherdemo.data.model.Coord
import com.xavier.weatherdemo.data.model.Main
import com.xavier.weatherdemo.data.model.Sys
import com.xavier.weatherdemo.data.model.Weather
import com.xavier.weatherdemo.data.model.WeatherResponse
import com.xavier.weatherdemo.data.model.Wind
import com.xavier.weatherdemo.data.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class WeatherRepositoryTest {

    private lateinit var weatherRepository: WeatherRepository

    @Mock
    private lateinit var weatherApi: WeatherApi

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        weatherRepository = WeatherRepository(weatherApi)
    }

    @Test
    fun `getWeatherByCity should return correct data`() = runBlockingTest {
        // Given a city name and mock response
        val city = "Toronto"
        val fakeWeatherResponse = createFakeWeatherResponse() // Helper method to create fake response

        // Mock the API call
        `when`(weatherApi.getWeatherByCity(city, "your_api_key")).thenReturn(fakeWeatherResponse)

        // Call the repository method
        val result = weatherRepository.getWeather(city, "your_api_key")

        // Verify the API was called
        verify(weatherApi).getWeatherByCity(city, "your_api_key")

        // Assert the result is correct
        assert(result == fakeWeatherResponse)
    }

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
