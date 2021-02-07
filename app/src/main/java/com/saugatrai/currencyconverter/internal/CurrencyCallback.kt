package com.saugatrai.currencyconverter.internal

/**
 * Callback when to show input amount enter for particular currency
 * */
interface CurrencyCallback {
    fun callback(rate: String, firstCurrency: String, secondCurrency: String)
}