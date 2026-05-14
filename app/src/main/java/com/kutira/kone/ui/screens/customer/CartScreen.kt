package com.kutira.kone.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kutira.kone.models.CartItem
import com.kutira.kone.ui.navigation.NavRoutes
import com.kutira.kone.ui.viewmodel.CartViewModel

@Composable
fun CartScreen(
    userId: String,
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {

    val cartItems by viewModel.cartItems.collectAsState()

    val selectedItems = remember {
        mutableStateListOf<String>()
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCartItems(userId)
    }

    val selectedCartItems =
        cartItems.filter {
            selectedItems.contains(it.cartItemId)
        }

    val selectedTotal =
        selectedCartItems.sumOf {
            it.totalPrice
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "My Cart",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(cartItems) { item ->

                CartItemCard(

                    item = item,

                    checked =
                        selectedItems.contains(
                            item.cartItemId
                        ),

                    onCheckedChange = { checked ->

                        if (checked) {

                            selectedItems.add(
                                item.cartItemId
                            )

                        } else {

                            selectedItems.remove(
                                item.cartItemId
                            )
                        }
                    },

                    onRemove = {

                        viewModel.removeCartItem(
                            item.cartItemId,
                            userId
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }

        Text(
            text = "Selected Total: ₹$selectedTotal",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        "cartItems",
                        selectedCartItems
                    )

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        "totalAmount",
                        selectedTotal
                    )

                navController.navigate(
                    NavRoutes.CHECKOUT
                )
            },

            modifier = Modifier.fillMaxWidth(),

            enabled = selectedCartItems.isNotEmpty()
        ) {

            Text("Proceed to Checkout")
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onRemove: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row {

                Checkbox(
                    checked = checked,

                    onCheckedChange =
                        onCheckedChange
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Column {

                    Text(
                        text = item.fabricTitle,
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Price: ₹${item.price}"
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "Total: ₹${item.totalPrice}"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = onRemove
            ) {

                Text("Remove")
            }
        }
    }
}