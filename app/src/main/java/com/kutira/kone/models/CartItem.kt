package com.kutira.kone.models

data class CartItem(
    val cartItemId: String = "",
    val customerId: String = "",
    val fabricId: String = "",
    val vendorId: String = "",
    val fabricTitle: String = "",
    val fabricImage: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0,
    val totalPrice: Double = 0.0
)