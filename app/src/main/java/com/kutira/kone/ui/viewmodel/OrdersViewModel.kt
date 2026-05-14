package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.models.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _orders =
        MutableStateFlow<List<Order>>(emptyList())

    val orders:
            StateFlow<List<Order>>
            = _orders.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {

        val currentUid =
            authRepository.currentUserId ?: ""

        viewModelScope.launch {

            try {

                val snapshot =
                    firestore.collection("orders")
                        .get()
                        .await()

                val list =
                    snapshot.documents
                        .mapNotNull {

                            it.toObject(
                                Order::class.java
                            )
                        }
                        .filter {

                            it.customerId ==
                                    currentUid
                        }
                        .sortedByDescending {
                            it.timestamp
                        }

                _orders.value = list

            } catch (_: Exception) {

            }
        }
    }
}