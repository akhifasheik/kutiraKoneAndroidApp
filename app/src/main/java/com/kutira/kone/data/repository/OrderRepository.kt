package com.kutira.kone.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kutira.kone.models.Order
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun placeOrder(
        order: Order
    ): Result<Unit> {

        return try {

            firestore.collection("orders")
                .document(order.orderId)
                .set(order)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}