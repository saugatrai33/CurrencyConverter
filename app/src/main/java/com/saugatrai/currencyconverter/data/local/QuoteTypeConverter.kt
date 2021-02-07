package com.saugatrai.currencyconverter.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Convert list of @{Quote} to String and vice-versa
 * */
class QuoteTypeConverter {
    @TypeConverter
    fun stringToQuotes(json: String?): List<Quote>? {
        val gson = Gson()
        val type = object : TypeToken<List<Quote?>?>() {}.type
        return gson.fromJson<List<Quote>>(json, type)
    }

    @TypeConverter
    fun quotesToString(list: List<Quote?>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<Quote?>?>() {}.type
        return gson.toJson(list, type)
    }
}