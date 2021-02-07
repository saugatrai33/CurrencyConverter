package com.saugatrai.currencyconverter.internal

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.rx2androidnetworking.Rx2AndroidNetworking
import com.saugatrai.currencyconverter.R
import com.saugatrai.currencyconverter.data.local.Quote
import com.saugatrai.currencyconverter.data.local.QuoteDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

/***
 * Utility function for the app.
 * */
object  Utils {

    /**
     * Helper function to get response from server using FAN. After getting response, it sends
     * data to other function for further processing.
     *
     * @return disposable.
     * */
    fun getCurrencyHelper(
        quoteLiveData: MutableLiveData<List<Quote>>,
        errCurrency: MutableLiveData<String>,
        quoteDao: QuoteDao?,
        storeLocally: Boolean
    ): Disposable {
        return Rx2AndroidNetworking.get(GET_CURRENCY)
            .addQueryParameter(ACCESS_KEY, ACCESS_VALUE)
            .build()
            .jsonObjectObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ responseJsonObj ->
                run {
                    val jsonResponse = JSONObject(responseJsonObj.toString())
                    if (!isJsonValid(jsonResponse)) {
                        checkResponse(
                            jsonResponse,
                            quoteLiveData,
                            errCurrency,
                            quoteDao,
                            storeLocally
                        )
                    }
                }
            },
                { err -> errCurrency.postValue(err.localizedMessage) })
    }

    /**
     * Get conversion amount from server
     *
     * @param from the currency source
     *
     * @param to the currency destination
     *
     * @param amount the amount from user input
     *
     * */
    fun convertRateHelper(
        from: String, to: String, amount: String,
        quoteLiveData: MutableLiveData<String>,
        errCurrency: MutableLiveData<String>,
    ): Disposable {
        return Rx2AndroidNetworking.get(URL_CONVERT)
            .addQueryParameter(ACCESS_KEY, ACCESS_VALUE)
            .addQueryParameter(QUERY_FROM, from)
            .addQueryParameter(QUERY_To, to)
            .addQueryParameter(QUERY_AMOUNT, amount)
            .build()
            .jsonObjectObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ responseJsonObj ->
                run {
                    val jsonResponse = JSONObject(responseJsonObj.toString())
                    parseConvertCurrency(
                        jsonResponse,
                        quoteLiveData,
                        errCurrency,
                    )
                }
            },
                { err -> errCurrency.postValue(err.localizedMessage) })
    }

    /**
     * Get the currencies of passed source
     *
     * @param source the source type such as CAD
     * */
    fun getSourceCurrencyHelper(
        source: String,
        quoteLiveData: MutableLiveData<List<Quote>>,
        errCurrency: MutableLiveData<String>,
        quoteDao: QuoteDao?,
        storeLocally: Boolean
    ): Disposable {
        return Rx2AndroidNetworking.get(URL_SOURCE)
            .addQueryParameter(ACCESS_KEY, ACCESS_VALUE)
            .addQueryParameter(QUERY_KEY_SOURCE, source)
            .build()
            .jsonObjectObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ responseJsonObj ->
                run {
                    val jsonObject = JSONObject(responseJsonObj.toString())
                    checkResponse(
                        jsonObject,
                        quoteLiveData,
                        errCurrency,
                        quoteDao,
                        storeLocally
                    )
                }
            },
                { err -> errCurrency.postValue(err.localizedMessage) })
    }

    /**
     * Check response which is obtained upon server request.
     * If the response is successful, then parse and add to {quoteLiveData}
     * If the response contains error json object, then parse and post to {errCurrency}
     *
     * @param jsonObject the response from the server as a {JSONObject}
     *
     * @param quoteLiveData success live data
     *
     * @param errCurrency error live data
     *
     * @param quoteDao Database Access Object to interact with database
     *
     * @param storeLocally whether quotes should be store in room database or not.
     *
     * */
    private fun checkResponse(
        jsonObject: JSONObject,
        quoteLiveData: MutableLiveData<List<Quote>>,
        errCurrency: MutableLiveData<String>,
        quoteDao: QuoteDao?,
        storeLocally: Boolean
    ) {
        val success = jsonObject.getBoolean(JSON_KEY_SUCCESS)
        if (success) {
            val quoteJsonObj = JSONObject(jsonObject.getString(JSON_KEY_QUOTES))
            parseSuccessResponse(
                quoteJsonObj, quoteLiveData,
                quoteDao = quoteDao, storeLocally = storeLocally
            )
        } else {
            val error = JSONObject(jsonObject.getString(JSON_KEY_ERROR))
            parseFailureResponse(error, errCurrency)
        }
    }

    /**
     * Parse success response and send response data to add to success live data
     *
     * @param jsonObject success response
     *
     * @param quoteLiveData live data of {Quote}
     *
     * */
    private fun parseSuccessResponse(
        jsonObject: JSONObject,
        quoteLiveData: MutableLiveData<List<Quote>>,
        storeLocally: Boolean,
        quoteDao: QuoteDao?
    ) {
        val quotes: MutableList<Quote> = mutableListOf()
        val quoteArray = jsonObject.names()
        for (i in 0 until quoteArray.length()) {
            val currency: String = quoteArray.getString(i)
            val rate: Double = jsonObject.getString(currency).toDouble()
            quotes.add(Quote(currency, rate))
        }
        addParseResponseToLiveData(quotes, quoteLiveData)
        if (storeLocally) {
            deleteAllCurrency(quoteDao!!)
            insertAllCurrency(quoteDao, quotes)
        }
    }

    /**
     * Add list of {Quote} to live data of {Quote}
     *
     * @param quoteList list of {Quote}
     *
     * @param quoteLiveData live data of {Quote}
     * */
    private fun addParseResponseToLiveData(
        quoteList: List<Quote>,
        quoteLiveData: MutableLiveData<List<Quote>>
    ) {
        quoteLiveData.postValue(quoteList)
    }

    /**
     * Parse failure response.
     *
     * @param jsonObject failure {JSONObject}
     *
     * @param errCurrency live data for error message.
     * */
    private fun parseFailureResponse(jsonObject: JSONObject, errCurrency: MutableLiveData<String>) {
        val info = jsonObject.getString(JSON_KEY_INFO)
        postErrCurrency(info, errCurrency)
    }

    /**
     * Add error message to live data.
     *
     * @param errMsg error message after parsed
     *
     * @param errCurrency live of error message.
     * */
    private fun postErrCurrency(errMsg: String, errCurrency: MutableLiveData<String>) {
        errCurrency.postValue(errMsg)
    }

    /**
     * Parse Converted Currency.
     *
     * @param jsonObject response as a json
     *
     * @param result observable string
     *
     * @param error observable for error message from api.
     * */
    private fun parseConvertCurrency(
        jsonObject: JSONObject,
        result: MutableLiveData<String>,
        error: MutableLiveData<String>
    ) {
        val success = jsonObject.getBoolean(JSON_KEY_SUCCESS)
        if (success) {
            parseSuccessConvertCurrency(jsonObject, result)
        } else {
            val errorJsonObj = jsonObject.getJSONObject(JSON_KEY_ERROR)
            parseFailureResponse(errorJsonObj, errCurrency = error)
        }
    }

    /**
     * Add convert currency result to observable.
     * */
    private fun parseSuccessConvertCurrency(
        jsonObject: JSONObject,
        result: MutableLiveData<String>
    ) {
        val resultValue = jsonObject.getString(JSON_KEY_RESULT)
        result.postValue(resultValue)
    }

    /**
     * Insert Data to room db
     *
     * @param quotes the list of quotes to be inserted.
     * */
    private fun insertAllCurrency(quoteDao: QuoteDao, quotes: List<Quote>) {
        quoteDao.insertQuotes(quotes)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    /**
     * Delete all currencies from local db.
     * */
    private fun deleteAllCurrency(quoteDao: QuoteDao) {
        quoteDao.deleteAllQuote()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    /**
     * Show dialog for exchanged rate
     *
     * */
    fun showExchangeRateDialog(context: Context, view: View) {
        // Build alert dialog
        val mDialogBuilder = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(true)
            .setNegativeButton(
                context.getString(R.string.dialog_text_cancel)
            ) { dialog, _ ->
                dialog.cancel()
            }

        mDialogBuilder.create().show()
    }

    /**
     * Show input rate dialog box
     *
     * @param currencyQuote the currency of particular country
     *
     * @param rate the base rate
     * */
    fun showInputAmountDialog(
        firstCurrency: String, secondCurrency: String,
        context: Context, view: View, callback: CurrencyCallback,
        enteredText: EditText
    ) {
        val mDialogBuilder = AlertDialog.Builder(context)
            .setView(view)
            .setTitle("$firstCurrency To $secondCurrency Conversion")
            .setCancelable(false)
            .setPositiveButton(
                context.getString(R.string.dialog_text_show)
            ) { dialog, _ ->
                dialog.dismiss()
                val inputRate = enteredText.text.toString().trim()
                callback.callback(inputRate, firstCurrency, secondCurrency)
            }
            .setNegativeButton(
                context.getString(R.string.dialog_text_cancel)
            ) { dialog, _ ->
                dialog.dismiss()
                showToast(context)
            }

        mDialogBuilder.create().show()
    }

    private fun showToast(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.msg_cancel_conversion_currency),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Check if a json is not empty
     * */
    fun isJsonValid(jsonObject: JSONObject): Boolean {
        return jsonObject.length() == 0
    }

}