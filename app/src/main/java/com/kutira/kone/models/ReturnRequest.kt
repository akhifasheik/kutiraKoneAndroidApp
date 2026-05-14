package com.kutira.kone.models

data class ReturnRequest(
    val returnId: String = "",
    val orderId: String = "",
    val customerId: String = "",
    val vendorId: String = "",
    val reason: String = "",
    val issueImage: String = "",
    val status: String = "RETURN_REQUESTED",
    val timestamp: Long = System.currentTimeMillis()
)