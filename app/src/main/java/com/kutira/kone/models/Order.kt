package com.kutira.kone.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Order(

    val orderId: String = "",

    val customerId: String = "",

    val customerPhone: String = "",

    val address: String = "",

    val paymentId: String = "",

    val totalAmount: Double = 0.0,

    val items: List<CartItem> = emptyList(),

    val status: String = "PLACED",

    @ServerTimestamp
    val timestamp: Date? = null
)