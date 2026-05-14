package com.kutira.kone.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

import com.kutira.kone.ui.viewmodel.ShippedProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippedProductsScreen(
    navController: NavController,
    viewModel: ShippedProductsViewModel = hiltViewModel()
) {

    val shipments by viewModel.shipments.collectAsState()

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Shipped Products")
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
                .padding(padding)
                .fillMaxSize(),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            items(shipments) { shipment ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            text = shipment.fabricTitle,

                            style =
                                MaterialTheme.typography.titleMedium,

                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text =
                                "Customer: ${shipment.customerPhone}"
                        )

                        Text(
                            text =
                                "Offered Item: ${shipment.offeredItem}"
                        )

                        Text(
                            text =
                                "Size: ${shipment.offeredSize}"
                        )

                        Text(
                            text =
                                "Color: ${shipment.offeredColor}"
                        )

                        Text(
                            text =
                                "Status: ${shipment.status}",

                            color =
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}