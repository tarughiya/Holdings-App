package com.example.holdings

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HoldingsViewModel: ViewModel() {
    private val apiService = RetrofitManager.api

    private val _holdings: MutableState<List<HoldingsData>> = mutableStateOf(emptyList())
    val holdings: State<List<HoldingsData>> = _holdings

    private val _holdingDataStatus: MutableState<HoldingDataStatus> = mutableStateOf(HoldingDataStatus.INITIAL)
    val holdingDataStatus: State<HoldingDataStatus> = _holdingDataStatus

    val isPortfolioVisible = mutableStateOf(false)

    val currentValueTotal = mutableDoubleStateOf(0.0)
    val totalInvestment = mutableDoubleStateOf(0.0)
    val todayPnl = mutableDoubleStateOf(0.0)
    val totalPnl = mutableDoubleStateOf(0.0)


    fun fetchHoldingsData() {
        viewModelScope.launch {
            try {
                _holdingDataStatus.value = HoldingDataStatus.INITIAL
                clearPortfolioData()
                val response: HoldingsDataResponse = apiService.getHoldings()
                if (!response.userHolding.isNullOrEmpty()) {
                    _holdings.value = response.userHolding
                    _holdings.value.forEach{
                        currentValueTotal.doubleValue +=  it.currentValue
                        totalInvestment.doubleValue +=  it.investmentValue
                        todayPnl.doubleValue +=  it.quantity*(it.close - it.ltp)
                    }
                    totalPnl.doubleValue = currentValueTotal.doubleValue - totalInvestment.doubleValue
                }
                _holdingDataStatus.value = HoldingDataStatus.SUCCESS
            } catch (e: Exception) {
                Log.e("Fetch data error",e.message.toString())
                _holdingDataStatus.value = HoldingDataStatus.FAILURE
            }
        }
    }
    fun togglePortFolioState(){
        isPortfolioVisible.value = isPortfolioVisible.value.not()
    }

    private fun clearPortfolioData(){
        _holdings.value = emptyList()
        currentValueTotal.doubleValue = 0.0
        totalInvestment.doubleValue = 0.0
        todayPnl.doubleValue = 0.0
        totalPnl.doubleValue = 0.0
    }
}