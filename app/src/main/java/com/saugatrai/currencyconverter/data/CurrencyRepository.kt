package com.saugatrai.currencyconverter.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.saugatrai.currencyconverter.data.local.Quote
import com.saugatrai.currencyconverter.data.local.QuoteDao
import com.saugatrai.currencyconverter.internal.Utils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Repository for Local and Network communication.
 * */
class CurrencyRepository(private val quoteDao: QuoteDao) {

    private val _currency by lazy { MutableLiveData<List<Quote>>() }
    val currency: LiveData<List<Quote>>
        get() = _currency

    private val _currencyError by lazy { MutableLiveData<String>() }
    val currencyError: LiveData<String>
        get() = _currencyError

    private val _currencyConversionResult by lazy { MutableLiveData<String>() }
    val currencyConversionResult: LiveData<String>
        get() = _currencyConversionResult

    /**
     * Get currencies from network.
     * */
    fun getCurrencies(storeLocally: Boolean): Disposable {
        return Utils.getCurrencyHelper(_currency, _currencyError, quoteDao, storeLocally)
    }

    /**
     * Every 30 minutes query the server and store data locally.
     *
     * @param source such as CAD, AUD that act as a filter currency
     *
     * @param storeLocally determines storing quotes locally or not
     * */
    fun getSourceCurrency(source: String, storeLocally: Boolean): Disposable {
        return Observable.interval(30, TimeUnit.MINUTES, Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribe { sourceCurrencyHelper(source, storeLocally) }
    }

    /**
     * Helper function to get particular source like CAD only from server.
     * */
    private fun sourceCurrencyHelper(source: String, storeLocally: Boolean): Disposable {
        return Utils.getSourceCurrencyHelper(
            source,
            _currency,
            _currencyError,
            quoteDao,
            storeLocally
        )
    }

    /**
     * Exchange currency given amount.
     *
     * @param from source currency
     *
     * @param to destination currency
     *
     * @param amount amount entered by user.
     * */
    fun currencyConvert(from: String, to: String, amount: String): Disposable {
        return Utils.convertRateHelper(from, to, amount, _currencyConversionResult, _currencyError)
    }

}