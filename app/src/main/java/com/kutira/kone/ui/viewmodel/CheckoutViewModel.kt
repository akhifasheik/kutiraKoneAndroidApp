package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.OrderRepository
import com.kutira.kone.models.CartItem
import com.kutira.kone.models.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import com.kutira.kone.data.repository.CartRepository

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _orderPlaced =
        MutableStateFlow(false)

    val orderPlaced:
            StateFlow<Boolean>
            = _orderPlaced.asStateFlow()

    fun placeOrder(
        address: String,
        phone: String,
        paymentId: String,
        totalAmount: Double,
        items: List<CartItem>
    ) {

        viewModelScope.launch {

            val order = Order(

                orderId =
                    UUID.randomUUID().toString(),

                customerId =
                    authRepository.currentUserId.orEmpty(),

                customerPhone = phone,

                address = address,

                paymentId = paymentId,

                totalAmount = totalAmount,

                items = items
            )

            val result =
                orderRepository.placeOrder(order)

            if (result.isSuccess) {

                items.forEach {

                    cartRepository.removeCartItem(
                        it.cartItemId
                    )
                }
            }

            _orderPlaced.value =
                result.isSuccess
        }
    }
}