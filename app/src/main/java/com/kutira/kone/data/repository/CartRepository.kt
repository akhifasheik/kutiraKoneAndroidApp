package com.kutira.kone.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kutira.kone.models.CartItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun addToCart(cartItem: CartItem) {
        firestore.collection("cart")
            .document(cartItem.cartItemId)
            .set(cartItem)
            .await()
    }

    suspend fun getCartItems(userId: String): List<CartItem> {
        return firestore.collection("cart")
            .whereEqualTo("customerId", userId)
            .get()
            .await()
            .toObjects(CartItem::class.java)
    }

    suspend fun removeCartItem(cartItemId: String) {
        firestore.collection("cart")
            .document(cartItemId)
            .delete()
            .await()
    }

    suspend fun updateQuantity(
        cartItemId: String,
        quantity: Int,
        totalPrice: Double
    ) {

        firestore.collection("cart")
            .document(cartItemId)
            .update(
                mapOf(
                    "quantity" to quantity,
                    "totalPrice" to totalPrice
                )
            )
            .await()
    }

    suspend fun clearCart(userId: String) {
        val cartItems = firestore.collection("cart")
            .whereEqualTo("customerId", userId)
            .get()
            .await()

        for (document in cartItems.documents) {
            document.reference.delete().await()
        }
    }
}