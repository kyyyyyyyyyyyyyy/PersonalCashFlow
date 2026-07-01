package com.example.cashflow.data.remote

import com.example.cashflow.Config
import com.example.cashflow.domain.GoldPrice
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MetalApi {
    @GET("api/XAU/USD")
    suspend fun getGoldPrice(): GoldPrice
}

object MetalClient {
    val api: MetalApi by lazy {
        val auth = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-access-token", Config.GOLD_API_KEY)
                .build()
            chain.proceed(request)
        }
        val client = OkHttpClient.Builder().addInterceptor(auth).build()
        Retrofit.Builder()
            .baseUrl("https://www.goldapi.io/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MetalApi::class.java)
    }
}
