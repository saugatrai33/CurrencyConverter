package com.saugatrai.currencyconverter.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.saugatrai.currencyconverter.data.CurrencyRepository
import com.saugatrai.currencyconverter.data.local.Quote
import io.reactivex.disposables.CompositeDisposable

/**
 * Communicate between @{MainActivity} and @{CurrencyRepository}
 * */
class CurrencyViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    val mAllCurrency: LiveData<List<Quote>> = currencyRepository.currency

    val mCurrencyError: LiveData<String> = currencyRepository.currencyError

    val mCurrencyResult: LiveData<String> = currencyRepository.currencyConversionResult

    fun getCurrency(storeLocally: Boolean) {
        compositeDisposable.add(currencyRepository.getCurrencies(storeLocally))
    }

    fun convertCurrency(from: String, to: String, rate: String) {
        currencyRepository.currencyConvert(from, to, rate)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}