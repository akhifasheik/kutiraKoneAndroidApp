package com.kutira.kone.models
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
data class ShippedProduct(

    @ServerTimestamp
    val timestamp: Date? = null,

    val shipmentId: String = "",

    val tradeId: String = "",

    val vendorId: String = "",

    val customerId: String = "",

    val fabricId: String = "",

    val fabricTitle: String = "",

    val customerPhone: String = "",

    val offeredItem: String = "",

    val offeredSize: String = "",

    val offeredColor: String = "",

    val status: String = "",

    val shippedAt: Long = System.currentTimeMillis()
)