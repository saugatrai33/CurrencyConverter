package com.saugatrai.currencyconverter.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saugatrai.currencyconverter.R
import com.saugatrai.currencyconverter.data.local.Quote

/**
 * Adapter to populate quotes.
 *
 * @param mQuoteClickInteractor click listener when quote is clicked.
 *
 * @param quotes the pair list of quotes from server.
 * */

class QuoteAdapter(
    private val mQuoteClickInteractor: OnQuoteClickInteractor,
    private val quotes: List<Quote>
) :
    RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quote, parent, false)
        return QuoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quote = quotes[position]
        holder.currencyText.text = quote.currency
        holder.rateText.text = quote.rate.toString()
        holder.itemView.setOnClickListener { mQuoteClickInteractor.onQuoteClick(quote) }
    }

    override fun getItemCount(): Int {
        return quotes.size
    }

    /**
     * ViewHolder to cache views.
     * */
    class QuoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currencyText: TextView = view.findViewById(R.id.text_currency)
        val rateText: TextView = view.findViewById(R.id.text_rate)
    }

}