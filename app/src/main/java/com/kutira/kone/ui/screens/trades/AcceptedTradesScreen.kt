package com.kutira.kone.ui.screens.trades

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import com.kutira.kone.models.TradeStatus
import com.kutira.kone.ui.viewmodel.AcceptedTradesViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptedTradesScreen(
    navController: NavController,
    viewModel: AcceptedTradesViewModel = hiltViewModel()
) {

    val trades by viewModel.acceptedTrades.collectAsState()

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Accepted Trades")
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            items(trades) { trade ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = trade.fabricTitle,

                            style =
                                MaterialTheme.typography.titleMedium,

                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Customer: ${trade.senderPhone}"
                        )

                        Text(
                            text =
                                "Trade Message: ${trade.tradeMessage}"
                        )

                        if (trade.offeredItem.isNotBlank()) {

                            Text(
                                text =
                                    "Customer Offer: ${trade.offeredItem}",

                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (trade.offeredDescription.isNotBlank()) {

                            Text(
                                text =
                                    "Offer Description: ${trade.offeredDescription}"
                            )
                        }

                        Text(
                            text = "Order ID: ${trade.orderId}"
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        // PENDING
                        if (
                            trade.status ==
                            TradeStatus.PENDING
                        ) {

                            Text(
                                text =
                                    "Waiting for vendor approval",

                                color =
                                    MaterialTheme.colorScheme.primary
                            )
                        }

                        // ACCEPTED
                        if (
                            trade.status ==
                            TradeStatus.ACCEPTED
                        ) {

                            Text(
                                text =
                                    "Vendor accepted your trade request",

                                color =
                                    MaterialTheme.colorScheme.primary
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.markShipped(
                                        trade.id
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                Text(
                                    "I Have Shipped My Product"
                                )
                            }
                        }

                        // CUSTOMER SHIPPED
                        if (
                            trade.status ==
                            TradeStatus.CUSTOMER_SHIPPED
                        ) {

                            Text(
                                text =
                                    "You shipped your product. Waiting for vendor shipment.",

                                color =
                                    MaterialTheme.colorScheme.primary
                            )
                        }

                        // VENDOR SHIPPED
                        if (
                            trade.status ==
                            TradeStatus.VENDOR_SHIPPED
                        ) {

                            Text(
                                text =
                                    "Vendor shipped the fabric.",

                                color =
                                    MaterialTheme.colorScheme.primary
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            OutlinedButton(
                                onClick = {
                                    viewModel.completeTrade(
                                        trade.id
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                Text("Mark As Completed")
                            }
                        }

                        // REJECTED
                        if (
                            trade.status ==
                            TradeStatus.REJECTED
                        ) {

                            Text(
                                text =
                                    "Vendor rejected this trade request.",

                                color =
                                    MaterialTheme.colorScheme.error
                            )
                        }

                        // COMPLETED
                        if (
                            trade.status ==
                            TradeStatus.COMPLETED
                        ) {

                            Text(
                                text =
                                    "Trade completed successfully.",

                                color =
                                    MaterialTheme.colorScheme.primary,

                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}