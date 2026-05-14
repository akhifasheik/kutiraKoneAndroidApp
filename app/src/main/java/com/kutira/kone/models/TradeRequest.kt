package com.kutira.kone.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class TradeRequest(

    @DocumentId
    val id: String = "",

    val requestId: String = "",

    // CUSTOMER
    val senderId: String = "",
    val senderPhone: String = "",

    // VENDOR
    val receiverId: String = "",

    // FABRIC
    val fabricId: String = "",
    val fabricTitle: String = "",

    // CUSTOMER OFFER
    val offeredItem: String = "",
    val offeredDescription: String = "",
    val offeredSize: String = "",
    val offeredColor: String = "",
    val offeredImage: String = "",

    // MESSAGE
    val tradeMessage: String = "",

    // LOCATION
    val customerLocation: String = "",

    // ORDER
    val orderId: String = "",

    // STATUS
    val status: String = TradeStatus.PENDING,

    @ServerTimestamp
    val timestamp: Date? = null
)

object TradeStatus {

    // WAITING FOR VENDOR
    const val PENDING = "pending"

    // VENDOR ACCEPTED
    const val ACCEPTED = "accepted"

    // VENDOR REJECTED
    const val REJECTED = "rejected"

    // CUSTOMER SHIPPED ITEM
    const val CUSTOMER_SHIPPED = "customer_shipped"

    // VENDOR SHIPPED FABRIC
    const val VENDOR_SHIPPED = "vendor_shipped"

    // TRADE FINISHED
    const val COMPLETED = "completed"
}