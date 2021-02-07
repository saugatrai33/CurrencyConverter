package com.saugatrai.currencyconverter.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saugatrai.currencyconverter.CurrencyConverterApplication
import com.saugatrai.currencyconverter.R
import com.saugatrai.currencyconverter.data.local.Quote
import com.saugatrai.currencyconverter.internal.CurrencyCallback
import com.saugatrai.currencyconverter.internal.KEY_CURRENCY
import com.saugatrai.currencyconverter.internal.Utils
import com.saugatrai.currencyconverter.ui.adapter.OnQuoteClickInteractor
import com.saugatrai.currencyconverter.ui.adapter.QuoteAdapter
import com.saugatrai.currencyconverter.ui.exchangerate.ExchangeRateActivity
import com.saugatrai.currencyconverter.ui.viewmodel.CurrencyViewModel
import com.saugatrai.currencyconverter.ui.viewmodel.CurrencyViewModelFactory
import io.reactivex.Observable


/**
 * Show currency screen with their respective values.
 *
 * This activity also interact with the user like what to do when currency is selected.
 * */
class MainActivity : AppCompatActivity(), OnQuoteClickInteractor, CurrencyCallback {

    private val TAG: String by lazy { MainActivity::class.java.canonicalName }

    private val currencyViewModel: CurrencyViewModel by viewModels {
        CurrencyViewModelFactory((application as CurrencyConverterApplication).repository)
    }

    private lateinit var quoteAdapter: QuoteAdapter

    private lateinit var quoteList: RecyclerView

    private var selectedCurrency: String = ""

    private lateinit var mProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initObserver()
    }

    private fun initView() {
        mProgressBar = findViewById(R.id.progress_par)
        mProgressBar.visibility = View.VISIBLE
        quoteList = findViewById(R.id.quote_list)
        quoteList.layoutManager = GridLayoutManager(this, 2)

    }

    private fun initObserver() {
        currencyViewModel.mAllCurrency.observe(this,
            { currency ->
                run {
                    mProgressBar.visibility = View.GONE
                    quoteAdapter = QuoteAdapter(this, currency)
                    quoteList.adapter = quoteAdapter
                }
            })

        currencyViewModel.mCurrencyError.observe(this,
            { errorMsg ->
                run {
                    mProgressBar.visibility = View.GONE
                    exchangeRateDialogShow(errorMsg)
                }
            })

        currencyViewModel.mCurrencyResult
            .observe(this, { result ->
                run {
                    mProgressBar.visibility = View.GONE
                    exchangeRateDialogShow(result)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        currencyViewModel.getCurrency(false)
    }

    /**
     * Show input rate dialog box
     *
     * @param currencyQuote the currency of particular country
     *
     * @param rate the base rate
     * */
    private fun showInputAmountDialog(
        firstCurrency: String, secondCurrency:
        String, rate: Double
    ) {
        // inflate custom view to show in the alert dialog
        val mDialogView = layoutInflater.inflate(
            R.layout.custom_currency_enter_dialog,
            null
        )

        // EditText from custom view
        val amountText = mDialogView.findViewById<EditText>(R.id.text_currency)
        amountText.hint = rate.toString()

        // Show dialog
        Utils.showInputAmountDialog(
            firstCurrency, secondCurrency,
            this, mDialogView, this, amountText
        )
    }

    /**
     * Show dialog for exchanged rate
     *
     * @param result the exchange rate in string. If error, it will display error message to user.
     * */
    private fun exchangeRateDialogShow(result: String) {
        // inflate custom view to show in the alert dialog
        val mDialogView = layoutInflater.inflate(
            R.layout.custom_currency_result_dialog,
            null
        )
        Utils.showExchangeRateDialog(this, mDialogView)
        navToExchangeRate(mDialogView, result)
    }

    private fun navToExchangeRate(view: View, result: String) {
        val title = view.findViewById<TextView>(R.id.text_result)
        val btnViewExchangeRate = view.findViewById<Button>(R.id.btn_exchange_rate)
        title.text = result
        btnViewExchangeRate.setOnClickListener {
            val intent = Intent(
                this@MainActivity,
                ExchangeRateActivity::class.java
            )
            intent.putExtra(KEY_CURRENCY, selectedCurrency)
            startActivity(intent)
        }
    }

    /**
     * Connect to currency conversion endpoint.
     *
     *@param from source currency
     *
     * @param to destination currency
     *
     * @param rate amount entered by user
     * */
    private fun requestCurrencyConversion(from: String, to: String, rate: String) {
        currencyViewModel.convertCurrency(from, to, rate)
    }

    override fun onQuoteClick(quote: Quote) {
        val first = quote.currency.substring(0, 3)
        val second = quote.currency.substring(3)
        val rate = quote.rate
        // store selected currency for future request as source
        this.selectedCurrency = first
        // show input rate dialog
        showInputAmountDialog(first, second, rate)
    }

    override fun callback(rate: String, firstCurrency: String, secondCurrency: String) {
        mProgressBar.visibility = View.VISIBLE
        requestCurrencyConversion(firstCurrency, secondCurrency, rate = rate)
    }
}