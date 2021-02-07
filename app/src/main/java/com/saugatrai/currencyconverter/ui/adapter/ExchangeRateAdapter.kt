package com.saugatrai.currencyconverter.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saugatrai.currencyconverter.R
import com.saugatrai.currencyconverter.data.local.Quote

/**
 * Adapter for the source quote list.
 * */
class ExchangeRateAdapter(private val quotes: List<Quote>) :
    RecyclerView.Adapter<ExchangeRateAdapter.ExchangeRateViewHolder>() {

    class ExchangeRateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quoteText: TextView = view.findViewById(R.id.text_source_currency)
        val rateText: TextView = view.findViewById(R.id.text_rate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quote_soure, parent, false)
        return ExchangeRateViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExchangeRateViewHolder, position: Int) {
        val quote = quotes[position]
        holder.quoteText.text = quote.currency
        holder.rateText.text = quote.rate.toString()
    }

    override fun getItemCount(): Int {
        return quotes.size
    }
}