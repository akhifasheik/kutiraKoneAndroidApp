package com.kutira.kone.ui.screens.trades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutira.kone.models.TradeStatus
import com.kutira.kone.ui.viewmodel.CustomerTradesViewModel

@Composable
fun CustomerTradesScreen(
    viewModel: CustomerTradesViewModel = hiltViewModel()
) {

    val requests by viewModel.requests.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(requests) { request ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        request.fabricTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text("Your Offer: ${request.offeredItem}")

                    Text("Message: ${request.tradeMessage}")

                    when (request.status) {

                        TradeStatus.PENDING -> {
                            Text(
                                "Status: Pending",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        TradeStatus.ACCEPTED -> {
                            Text(
                                "Status: Accepted",
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text("Order ID: ${request.orderId}")
                        }

                        TradeStatus.REJECTED -> {
                            Text(
                                "Status: Rejected",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}