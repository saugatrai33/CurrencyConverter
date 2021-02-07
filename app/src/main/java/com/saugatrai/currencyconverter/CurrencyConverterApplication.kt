package com.saugatrai.currencyconverter

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.gsonparserfactory.GsonParserFactory
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.facebook.stetho.DumperPluginsProvider
import com.facebook.stetho.Stetho
import com.facebook.stetho.dumpapp.DumperPlugin
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.saugatrai.currencyconverter.data.CurrencyRepository
import com.saugatrai.currencyconverter.data.local.CurrencyConverterDatabase
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient

open class CurrencyConverterApplication : Application() {
    val database by lazy { CurrencyConverterDatabase.getDatabase(this) }
    val repository by lazy { CurrencyRepository(database.quoteDao()) }

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        val okHttpClient = OkHttpClient().newBuilder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()

        AndroidNetworking.initialize(applicationContext, okHttpClient)
        AndroidNetworking.setParserFactory(GsonParserFactory())

    }
}