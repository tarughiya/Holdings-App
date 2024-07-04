package com.example.holdings

import retrofit2.http.GET

interface HoldingsService {
    @GET("/")
    suspend fun getHoldings(): HoldingsDataResponse
}