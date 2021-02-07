package com.saugatrai.currencyconverter.internal

import com.saugatrai.currencyconverter.BuildConfig

/**
 * Various Constants for the app.
 * */

/**
 * Constants for network
 * */
const val BASE_URL = "http://api.currencylayer.com/"
const val URL_LIVE = "live"
const val URL_CONVERT = "${BASE_URL}convert"
const val QUERY_FROM = "from"
const val QUERY_To = "to"
const val QUERY_AMOUNT = "amount"
const val QUERY_KEY_SOURCE = "source"
const val GET_CURRENCY = "${BASE_URL}$URL_LIVE"
const val URL_SOURCE = "${BASE_URL}${URL_LIVE}"
const val ACCESS_KEY = "access_key"
const val ACCESS_VALUE = BuildConfig.CURRENCYLAYER_ACCESS_KEY
const val JSON_KEY_SUCCESS = "success"
const val JSON_KEY_QUOTES = "quotes"
const val JSON_KEY_ERROR = "error"
const val JSON_KEY_INFO = "info"
const val JSON_KEY_RESULT = "result"

/**
 * Room Db constants
 * */
const val ROOM_DB_NAME = "currency_db"

/**
 * Constants within app uses
 * */
const val KEY_CURRENCY = "key_currency"