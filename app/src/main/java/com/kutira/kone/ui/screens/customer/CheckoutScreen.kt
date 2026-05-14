package com.kutira.kone.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kutira.kone.models.CartItem
import android.app.Activity
import android.widget.Toast
import com.razorpay.Checkout
import org.json.JSONObject
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutira.kone.ui.viewmodel.CheckoutViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CheckoutScreen(
navController: NavController,
cartItems: List<CartItem>,
totalAmount: Double,
checkoutViewModel: CheckoutViewModel = hiltViewModel(),
    onPlaceOrder: (
        address: String,
        phone: String,
        paymentId: String
    ) -> Unit
) {

    var address by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    val activity = LocalContext.current as Activity

    val checkout = Checkout()

    val orderPlaced by
    checkoutViewModel.orderPlaced
        .collectAsStateWithLifecycle()

    LaunchedEffect(orderPlaced) {

        if (orderPlaced) {

            Toast.makeText(
                activity,
                "Order Placed Successfully!",
                Toast.LENGTH_LONG
            ).show()

            navController.popBackStack()
        }
    }

    checkout.setKeyID(
        "rzp_test_SpAxqZuXBAVLUU"
    )

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Checkout",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = {
                address = it
            },

            modifier = Modifier.fillMaxWidth(),

            label = {
                Text("Delivery Address")
            },

            singleLine = false
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
            },

            modifier = Modifier.fillMaxWidth(),

            label = {
                Text("Phone Number")
            },

            singleLine = true
        )

        if (errorMessage.isNotEmpty()) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Order Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(cartItems) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            text = item.fabricTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Quantity: ${item.quantity}")

                        Text("Price: ₹${item.price}")

                        Text(
                            text = "Total: ₹${item.totalPrice}",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Payment Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Total Amount: ₹$totalAmount",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                when {

                    address.isBlank() -> {
                        errorMessage = "Please enter delivery address"
                    }

                    phone.isBlank() -> {
                        errorMessage = "Please enter phone number"
                    }

                    phone.length < 10 -> {
                        errorMessage = "Please enter valid phone number"
                    }

                    else -> {

                        errorMessage = ""
                        isLoading = true

                        try {

                            val options = JSONObject()

                            options.put(
                                "name",
                                "Kutira Kone"
                            )

                            options.put(
                                "description",
                                "Fabric Purchase"
                            )

                            options.put(
                                "currency",
                                "INR"
                            )

                            options.put(
                                "amount",
                                (totalAmount * 100).toInt()
                            )

                            options.put(
                                "prefill.contact",
                                phone
                            )

                            checkout.setImage(android.R.drawable.ic_menu_gallery)

                            checkout.open(
                                activity,
                                options
                            )

                            checkoutViewModel.placeOrder(
                                address = address,
                                phone = phone,
                                paymentId = "RAZORPAY_SUCCESS",
                                totalAmount = totalAmount,
                                items = cartItems
                            )

                            Toast.makeText(
                                activity,
                                "Order Placed Successfully!",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.popBackStack()

                        } catch (e: Exception) {

                            isLoading = false

                            Toast.makeText(
                                activity,
                                e.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {

            if (isLoading) {

                CircularProgressIndicator()

            } else {

                Text(
                    text = "Make Payment",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}