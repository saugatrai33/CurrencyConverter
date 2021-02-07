package com.saugatrai.currencyconverter.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
interface QuoteDao {

    @Query("SELECT * FROM quote ORDER BY currency DESC")
    fun getAllQuote(): Flowable<List<Quote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuotes(quotes: List<Quote>): Completable

    @Query("DELETE FROM quote")
    fun deleteAllQuote(): Completable


}