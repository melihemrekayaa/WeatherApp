package com.example.weatherapp.data.mappers

import WeatherType
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherapp.data.remote.WeatherDataDto
import com.example.weatherapp.data.remote.WeatherDto
import com.example.weatherapp.domain.weather.WeatherData
import com.example.weatherapp.domain.weather.WeatherInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private data class IndexedWeatherData(
    val index: Int,
    val data : WeatherData
)
@RequiresApi(Build.VERSION_CODES.O)
fun WeatherDataDto.toWeatherDataMap() : Map<Int,List<WeatherData>> {
    return time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val weatherCode = weatherCodes[index]
        val windSpeed = windSpeeds[index]
        val pressure = pressures[index]
        val humidity = humidities[index]
       IndexedWeatherData(
           index = index,
           data =  WeatherData(
               time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
               temperatureCelsius = temperature,
               pressure = pressure,
               windSpeed = windSpeed,
               humidity = humidity,
               weatherType = WeatherType.fromWMO(weatherCode)
           )
       )
    }.groupBy {
        it.index / 24
    }.mapValues { it ->
        it.value.map {
            it.data
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun WeatherDto.toWeatherInfo() : WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()
    val currentWeatherData = weatherDataMap[0]?.find {
        val hour = if (now.minute >= 30) now.hour + 1 else now.hour
        it.time.hour == hour
    }
    return WeatherInfo(
        weatherDataPerDay = weatherDataMap,
        currentWeatherData = currentWeatherData
    )
}