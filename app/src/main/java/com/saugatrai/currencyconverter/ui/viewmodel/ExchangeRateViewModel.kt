package com.saugatrai.currencyconverter.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.saugatrai.currencyconverter.data.CurrencyRepository
import com.saugatrai.currencyconverter.data.local.Quote
import io.reactivex.disposables.CompositeDisposable

/**
 * Communicate between @{ExchangeRateActivity} and @{CurrencyRepository}
 * */
class ExchangeRateViewModel(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    val mAllCurrency: LiveData<List<Quote>> = currencyRepository.currency

    val mCurrencyError: LiveData<String> = currencyRepository.currencyError

    fun switchCurrency(source: String, storeLocally: Boolean) {
        compositeDisposable.add(currencyRepository.getSourceCurrency(source, storeLocally))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}