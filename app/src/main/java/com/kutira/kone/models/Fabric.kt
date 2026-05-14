package com.kutira.kone.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Fabric(
    @DocumentId
    val id: String = "",
    val imageUrl: String = "",
    val materialType: String = "",
    val color: String = "",
    val size: String = "",
    val description: String = "",
    val sellerId: String = "",
    val sellerPhone: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    @ServerTimestamp
    val createdAt: Date? = null,
    val available: Boolean = true,
    val price: Double = 0.0,
    val swapAvailable: Boolean = false
)
