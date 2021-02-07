package com.saugatrai.currencyconverter.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local Database table to store currency and respective rate.
 *
 *@param currency currency like USD
 *
 * @param rate the base exchange rate amount.
 * */

@Entity(tableName = "quote")
data class Quote(
    @PrimaryKey val currency: String,
    val rate: Double
)