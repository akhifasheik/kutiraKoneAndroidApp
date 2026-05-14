package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.repository.CartRepository
import com.kutira.kone.models.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addToCart(cartItem: CartItem) {
        viewModelScope.launch {
            repository.addToCart(cartItem)
        }
    }

    fun fetchCartItems(userId: String) {
        viewModelScope.launch {
            _cartItems.value = repository.getCartItems(userId)
        }
    }

    fun removeCartItem(
        cartItemId: String,
        userId: String
    ) {

        viewModelScope.launch {

            repository.removeCartItem(cartItemId)

            fetchCartItems(userId)
        }
    }

    fun updateQuantity(
        cartItem: CartItem,
        newQuantity: Int
    ) {

        if (newQuantity <= 0) return

        viewModelScope.launch {

            repository.updateQuantity(
                cartItemId = cartItem.cartItemId,
                quantity = newQuantity,
                totalPrice = cartItem.price * newQuantity
            )

            fetchCartItems(cartItem.customerId)
        }
    }

    fun clearCart(userId: String) {
        viewModelScope.launch {
            repository.clearCart(userId)
        }
    }

    fun calculateTotal(): Double {
        return _cartItems.value.sumOf {
            it.price * it.quantity
        }
    }
}