package com.kutira.kone.ui.screens.vendor

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.kutira.kone.ui.viewmodel.VendorOrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorOrdersScreen(
    viewModel: VendorOrdersViewModel =
        hiltViewModel()
) {

    val orders by
    viewModel.orders.collectAsState()

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Customer Orders")
                }
            )
        }
    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),

            contentPadding =
                PaddingValues(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            items(orders) { order ->

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier =
                            Modifier.padding(16.dp)
                    ) {

                        Text(
                            text =
                                "Order ID: ${order.orderId}",

                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Customer Phone: ${order.customerPhone}"
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Address: ${order.address}"
                        )

                        Text(
                            text =
                                "Amount: ₹${order.totalAmount}"
                        )

                        Text(
                            text =
                                "Items: ${order.items.size}"
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Ordered Items:",
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        order.items.forEach { item ->

                            Text(
                                text =
                                    "• ${item.fabricTitle} - ₹${item.totalPrice}"
                            )
                        }

                        Text(
                            text =
                                "Status: ${order.status}"
                        )

                        Button(

                            onClick = {

                                viewModel.markDelivered(
                                    order.orderId
                                )
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            enabled =
                                order.status != "DELIVERED"
                        ) {

                            Text(

                                if (
                                    order.status == "DELIVERED"
                                ) {
                                    "Delivered"
                                } else {
                                    "Mark Delivered"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}