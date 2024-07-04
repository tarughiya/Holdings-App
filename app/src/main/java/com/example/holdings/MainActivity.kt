package com.example.holdings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    private val holdingAppViewModel: HoldingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HoldingsScreen(holdingAppViewModel)
        }
    }
}

@Composable
fun HoldingsScreen(holdingAppViewModel: HoldingsViewModel) {
    DisposableEffect(Unit) {
        holdingAppViewModel.holdings
        onDispose {}
    }


    Surface(
        color = Color.White
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(colorResource(id = R.color.primary_color))
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            when (holdingAppViewModel.holdingDataStatus.value) {
                HoldingDataStatus.INITIAL -> Box(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                HoldingDataStatus.SUCCESS -> LazyColumn(
                    Modifier
                        .weight(1f)
                ) {
                    items(holdingAppViewModel.holdings.value) { holding ->
                        HoldingsCard(holdingsData = holding)
                    }
                }

                HoldingDataStatus.FAILURE -> Box(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text(text = stringResource(id = R.string.error_fetching_data))
                        Spacer(modifier = Modifier.height(16.dp))
                        Image(
                            painter = painterResource(id = R.drawable.baseline_autorenew_24),
                            contentDescription = "arrow",
                            modifier = Modifier
                                .clickable {
                                    holdingAppViewModel.fetchHoldingsData()
                                }
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
            Surface(shadowElevation = 16.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                        contentDescription = "arrow",
                        modifier = Modifier
                            .size(30.dp)
                            .rotate(if (holdingAppViewModel.isPortfolioVisible.value) 0f else 180f)
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = { holdingAppViewModel.togglePortFolioState() })
                            }
                    )
                    AnimatedVisibility(visible = holdingAppViewModel.isPortfolioVisible.value) {
                        Column {
                            PortfolioRow(
                                titleId = R.string.current_value,
                                value = holdingAppViewModel.currentValueTotal.doubleValue
                            )
                            PortfolioRow(
                                titleId = R.string.total_investment,
                                value = holdingAppViewModel.totalInvestment.doubleValue
                            )
                            PortfolioRow(
                                titleId = R.string.todays_profit,
                                value = holdingAppViewModel.todayPnl.doubleValue
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    PortfolioRow(
                        titleId = R.string.profit_n_loss,
                        value = holdingAppViewModel.totalPnl.doubleValue
                    )
                }
            }
        }
    }
}

@Composable
fun PortfolioRow(titleId: Int, value: Double) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(titleId),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
        )
        Text(text = String.format("₹${DecimalFormat("0.00").format(value)}"))
    }
}

@Composable
fun HoldingsCard(holdingsData: HoldingsData) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 1.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = AbsoluteAlignment.Left
        ) {
            Text(
                text = holdingsData.symbol,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = holdingsData.quantity.toString())
        }
        Column(
            horizontalAlignment = AbsoluteAlignment.Right
        ) {
            Text(text = "LTP: ₹ ${holdingsData.ltp}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "P/L: ₹ ${DecimalFormat("0.00").format(holdingsData.pnl)}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HoldingsCardPreview() {
    HoldingsCard(
        holdingsData = HoldingsData(
            symbol = "TCS",
            quantity = 10,
            ltp = 3250.5,
            avgPrice = 2480.3,
            close = 3312.0,
        )
    )
}