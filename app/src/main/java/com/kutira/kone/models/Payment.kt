package com.kutira.kone.models

data class Payment(
    val paymentId: String = "",
    val orderId: String = "",
    val customerId: String = "",
    val amount: Double = 0.0,
    val paymentMethod: String = "",
    val paymentStatus: String = "",
    val timestamp: Long = System.currentTimeMillis()
)