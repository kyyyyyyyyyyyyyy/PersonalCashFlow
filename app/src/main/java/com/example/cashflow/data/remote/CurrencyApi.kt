package com.example.cashflow.data.remote

import com.example.cashflow.domain.CurrencyResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("latest")
    suspend fun getLatestRates(
        @Query("from") base: String = "USD"
    ): CurrencyResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://api.frankfurter.app/"

    val api: CurrencyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }
}
