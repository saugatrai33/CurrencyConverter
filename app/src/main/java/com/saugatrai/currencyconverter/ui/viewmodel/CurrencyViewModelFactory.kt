package com.saugatrai.currencyconverter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saugatrai.currencyconverter.data.CurrencyRepository

/**
 * Factory class to generate viewmodels.
 * */
class CurrencyViewModelFactory(private val repository: CurrencyRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            return CurrencyViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(ExchangeRateViewModel::class.java)) {
            return ExchangeRateViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}