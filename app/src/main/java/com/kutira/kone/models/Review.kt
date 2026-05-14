package com.kutira.kone.models

data class Review(
    val reviewId: String = "",
    val fabricId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val rating: Float = 0f,
    val reviewText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)