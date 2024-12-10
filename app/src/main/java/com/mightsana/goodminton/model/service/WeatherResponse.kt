package com.mightsana.goodminton.model.service

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain,
    val clouds: Clouds,
    val dt: Int,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Int,
    val sunset: Int
)

data class Clouds(
    val all: Int
)

data class Rain(
    val `1h`: Double
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Coord(
    val lat: Double,
    val lon: Double
)

data class Weather(
    val id: Int,
    val main: WeatherCondition,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int,
    val grnd_level: Int
)

@Suppress("unused")
enum class WeatherCondition {
    Thunderstorm,
    Drizzle,
    Rain,
    Snow,
    Atmosphere,
    Clear,
    Clouds
}