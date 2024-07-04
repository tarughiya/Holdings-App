package com.example.holdings

import com.google.gson.annotations.SerializedName

data class HoldingsDataResponse (
    @SerializedName("userHolding")
    val userHolding: List<HoldingsData>? = null
)

data class  HoldingsData(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("ltp")
    val ltp: Double,
    @SerializedName("avgPrice")
    val avgPrice: Double,
    @SerializedName("close")
    val close: Double,
){
    val currentValue: Double
        get() = quantity * ltp
    val investmentValue: Double
        get() = quantity * avgPrice
    val pnl: Double
        get() = currentValue - investmentValue
}

enum class HoldingDataStatus{
    INITIAL,
    SUCCESS,
    FAILURE
}