package com.saugatrai.currencyconverter.ui.exchangerate

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saugatrai.currencyconverter.CurrencyConverterApplication
import com.saugatrai.currencyconverter.R
import com.saugatrai.currencyconverter.internal.KEY_CURRENCY
import com.saugatrai.currencyconverter.ui.adapter.ExchangeRateAdapter
import com.saugatrai.currencyconverter.ui.viewmodel.CurrencyViewModelFactory
import com.saugatrai.currencyconverter.ui.viewmodel.ExchangeRateViewModel

/**
 *
 * Shows available exchange rate for particular currency.
 * */

class ExchangeRateActivity : AppCompatActivity() {

    private val TAG: String by lazy { ExchangeRateActivity::class.java.canonicalName }

    private val exchangeRateViewModel: ExchangeRateViewModel by viewModels {
        CurrencyViewModelFactory((application as CurrencyConverterApplication).repository)
    }

    private lateinit var source: String

    private lateinit var quoteList: RecyclerView

    private lateinit var quoteAdapter: ExchangeRateAdapter

    private lateinit var mProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_rate)
        source = intent.extras!!.getString(KEY_CURRENCY)!!
        initViews()
        initObserver()
    }

    private fun initViews() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mProgressBar = findViewById(R.id.progress_par)
        mProgressBar.visibility = View.VISIBLE
        quoteList = findViewById(R.id.quote_list_source)
        quoteList.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL, false
        )
    }

    private fun initObserver() {
        exchangeRateViewModel.mAllCurrency.observe(this,
            { currency ->
                run {
                    mProgressBar.visibility = View.GONE
                    quoteAdapter = ExchangeRateAdapter(currency)
                    quoteList.apply {
                        adapter = quoteAdapter
                    }
                }
            })

        exchangeRateViewModel.mCurrencyError.observe(this,
            { errMsg ->
                run {
                    mProgressBar.visibility = View.GONE
                    Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        exchangeRateViewModel.switchCurrency(source, true)
    }
}