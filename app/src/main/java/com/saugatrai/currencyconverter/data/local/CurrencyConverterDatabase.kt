package com.saugatrai.currencyconverter.data.local

import android.content.Context
import androidx.room.*
import com.saugatrai.currencyconverter.internal.ROOM_DB_NAME

@Database(entities = [Quote::class], version = 1, exportSchema = false)
@TypeConverters(QuoteTypeConverter::class)
abstract class CurrencyConverterDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao

    companion object {
        @Volatile
        private var INSTANCE: CurrencyConverterDatabase? = null

        fun getDatabase(context: Context): CurrencyConverterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyConverterDatabase::class.java,
                    ROOM_DB_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }


}