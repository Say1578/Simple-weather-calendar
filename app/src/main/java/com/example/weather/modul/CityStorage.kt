package com.example.weather.modul

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CityStorage {
    private const val PREFS_NAME = "weather_app_prefs"
    private const val KEY_SAVED_CITIES = "saved_cities"
    private const val KEY_SELECTED_CITY = "selected_city"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSavedCities(context: Context): MutableList<String> {
        val prefs = getPrefs(context)
        val json = prefs.getString(KEY_SAVED_CITIES, null)
        return if (json != null) {
            try {
                Json.decodeFromString(ListSerializer(String.serializer()), json).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
    }

    fun addCity(context: Context, city: String) {
        val cities = getSavedCities(context)
        if (!cities.contains(city)) {
            cities.add(0, city)
            saveCities(context, cities)
        }
    }

    fun removeCity(context: Context, city: String) {
        val cities = getSavedCities(context)
        cities.remove(city)
        saveCities(context, cities)
        if (getSelectedCity(context) == city) {
            getPrefs(context).edit().putString(KEY_SELECTED_CITY, cities.firstOrNull()).apply()
        }
    }

    private fun saveCities(context: Context, cities: List<String>) {
        val prefs = getPrefs(context)
        val json = Json.encodeToString(ListSerializer(String.serializer()), cities)
        prefs.edit().putString(KEY_SAVED_CITIES, json).apply()
    }

    fun setSelectedCity(context: Context, city: String) {
        val prefs = getPrefs(context)
        prefs.edit().putString(KEY_SELECTED_CITY, city).apply()
        addCity(context, city)
    }

    fun getSelectedCity(context: Context): String? {
        val prefs = getPrefs(context)
        return prefs.getString(KEY_SELECTED_CITY, getSavedCities(context).firstOrNull())
    }
}
