package com.example.holdings

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {
    val api: HoldingsService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://35dee773a9ec441e9f38d5fc249406ce.api.mockbin.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(HoldingsService::class.java)
    }
}