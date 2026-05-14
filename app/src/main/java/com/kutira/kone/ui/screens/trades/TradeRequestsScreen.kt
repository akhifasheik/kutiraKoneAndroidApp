package com.kutira.kone.ui.screens.trades

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import coil.compose.AsyncImage
import com.kutira.kone.models.TradeStatus
import com.kutira.kone.ui.components.EmptyState
import com.kutira.kone.ui.viewmodel.TradeRequestsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeRequestsScreen(
    viewModel: TradeRequestsViewModel = hiltViewModel()
) {

    val models by viewModel.incomingModels.collectAsState()
    val message by viewModel.message.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Incoming Trade Requests",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        if (models.isEmpty()) {

            EmptyState(
                title = "No trade requests",
                subtitle = "Incoming customer trade requests will appear here.",
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )

            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            item {

                message?.let {

                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(
                models,
                key = { it.request.id }
            ) { model ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        val fabric = model.fabric
                        val trade = model.request

                        if (fabric != null) {

                            AsyncImage(
                                model = fabric.imageUrl,
                                contentDescription = null,

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )

                            Text(
                                text = fabric.materialType,

                                style =
                                    MaterialTheme.typography.titleMedium,

                                fontWeight = FontWeight.Bold
                            )

                        } else {

                            Text(
                                text = "Fabric ${trade.fabricId}",

                                style =
                                    MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Requested by: ${trade.senderPhone}",

                            style =
                                MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Fabric Title: ${trade.fabricTitle}",

                            style =
                                MaterialTheme.typography.bodyMedium,

                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Customer Offer",

                            style =
                                MaterialTheme.typography.titleMedium,

                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = trade.offeredItem,

                            style =
                                MaterialTheme.typography.bodyLarge
                        )

                        if (trade.offeredDescription.isNotBlank()) {

                            Text(
                                text = trade.offeredDescription,

                                style =
                                    MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (trade.offeredSize.isNotBlank()) {

                            Text(
                                text = "Size: ${trade.offeredSize}",

                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (trade.offeredColor.isNotBlank()) {

                            Text(
                                text = "Color: ${trade.offeredColor}",

                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Trade Message",

                            style =
                                MaterialTheme.typography.labelMedium,

                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = trade.tradeMessage,

                            style =
                                MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Status: ${trade.status.uppercase()}",

                            style =
                                MaterialTheme.typography.titleSmall,

                            color =
                                MaterialTheme.colorScheme.primary,

                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // PENDING
                        if (trade.status == TradeStatus.PENDING) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),

                                horizontalArrangement =
                                    Arrangement.SpaceBetween
                            ) {

                                Button(
                                    onClick = {
                                        viewModel.accept(trade)
                                    },

                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text("Accept")
                                }

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                OutlinedButton(
                                    onClick = {
                                        viewModel.reject(trade)
                                    },

                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text("Reject")
                                }
                            }
                        }

                        // CUSTOMER SHIPPED
                        if (
                            trade.status ==
                            TradeStatus.CUSTOMER_SHIPPED
                        ) {

                            Button(
                                onClick = {

                                    viewModel.markVendorShipped(
                                        trade
                                    )
                                },

                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Text("Ship Fabric")
                            }
                        }

                        // ACCEPTED
                        if (
                            trade.status ==
                            TradeStatus.ACCEPTED
                        ) {

                            Text(
                                text =
                                    "Waiting for customer to ship item.",

                                color =
                                    MaterialTheme.colorScheme.primary
                            )
                        }

                        // REJECTED
                        if (
                            trade.status ==
                            TradeStatus.REJECTED
                        ) {

                            Text(
                                text =
                                    "Trade request rejected.",

                                color =
                                    MaterialTheme.colorScheme.error
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
                                    MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}