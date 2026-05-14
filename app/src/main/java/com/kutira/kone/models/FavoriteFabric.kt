package com.kutira.kone.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class FavoriteFabric(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val fabricId: String = "",
    @ServerTimestamp
    val addedAt: Date? = null
)
