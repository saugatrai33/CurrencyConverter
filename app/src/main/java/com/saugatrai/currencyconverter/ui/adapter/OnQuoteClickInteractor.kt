package com.saugatrai.currencyconverter.ui.adapter

import com.saugatrai.currencyconverter.data.local.Quote

/**
 * Click interactor for quote click.
 * */

interface OnQuoteClickInteractor {

    /**
     * @param quote the pair of currency and corresponding exchange rate
     * */
    fun onQuoteClick(quote: Quote)
}