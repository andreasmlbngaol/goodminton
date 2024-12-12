package com.mightsana.goodminton.model.component_model

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.mightsana.goodminton.model.service.RetrofitClient
import com.mightsana.goodminton.model.service.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(
    askForPermissionContent: @Composable (launchPermissionRequest: () -> Unit) -> Unit,
    onPermissionGranted: () -> Unit = {},
    onPermissionGrantedContent: @Composable () -> Unit
) {
    val permissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            onPermissionGranted()
        }
    }

    AnimatedContent(permissionState.status, label = "") { status ->
        when(status) {
            is PermissionStatus.Granted -> {
                onPermissionGrantedContent()
                onPermissionGranted()
            }
            is PermissionStatus.Denied -> {
                askForPermissionContent { permissionState.launchPermissionRequest() }
            }
        }
    }
}

@Composable
fun FetchLocationAndWeather(onWeatherDataFetched: (WeatherResponse) -> Unit) {
    val context = LocalContext.current
    RequestLocationPermission(
        askForPermissionContent = { Text("Permit") },
        onPermissionGranted = {
            fetchUserLocation(context) { latitude, longitude ->
                fetchWeatherData(latitude, longitude) { weatherData ->
                    onWeatherDataFetched(weatherData)
                }
            }
        },
    ) {
        Text("Location permission granted.")
    }
}


fun fetchUserLocation(context: Context, onLocationReceived: (latitude: Double, longitude: Double) -> Unit) {
    Log.d("FetchLocationAndWeather", "enter fetchUserLocation")
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            Log.d("FetchLocationAndWeather", "enter fusedLocationClient.lastLocation.addOnSuccessListener")
            onLocationReceived(location.latitude, location.longitude)
        } else {
            Log.d("FetchLocationAndWeather", "Location null")
            println("Location is null")
        }
    }.addOnFailureListener {
        println("Failed to get location: ${it.message}")
    }
}

fun fetchWeatherData(latitude: Double, longitude: Double, response: (WeatherResponse) -> Unit) {
    val apiKey = "88c30cc2e4751a865d9a774c68522896"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val rsp = RetrofitClient.instance.getCurrentWeather(latitude, longitude, apiKey)
            response(rsp)
        } catch (e: Exception) {
            Log.e("FetchLocationAndWeather", "Error fetching weather data: ${e.message}")
        }
    }
}

@Suppress("unused")
@Composable
fun WeatherApp() {
    var weatherData by remember { mutableStateOf("Fetching weather...") }
    Log.d("FetchLocationAndWeather", "enter WeatherApp")


    Scaffold { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            FetchLocationAndWeather { response ->
                weatherData = response.weather[0].main.toString()
            }

            Text(text = "Weather Info")
            Text(text = weatherData)
        }
    }
}
