package com.saugatrai.currencyconverter.internal

import org.json.JSONObject
import org.junit.Test

class UtilsTest {

    @Test
    fun getCurrencyHelper() {
    }

    @Test
    fun convertRateHelper() {
    }

    @Test
    fun getSourceCurrencyHelper() {
    }

    @Test
    fun showExchangeRateDialog() {
    }

    @Test
    fun showInputAmountDialog() {
    }

    @Test
    fun isJsonValid() {
        val json = "{\n" +
                "    \"success\": false,\n" +
                "    \"error\": {\n" +
                "        \"code\": 104,\n" +
                "        \"info\": \"Your monthly usage limit has been reached. Please upgrade your subscription plan.\"    \n" +
                "  }\n" +
                "}  "
        val result = Utils.isJsonValid(jsonObject = JSONObject(json))
        assert(!result)
    }
}