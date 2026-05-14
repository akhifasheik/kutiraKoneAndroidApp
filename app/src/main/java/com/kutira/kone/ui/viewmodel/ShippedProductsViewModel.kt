package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.models.ShippedProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltViewModel
class ShippedProductsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _shipments =
        MutableStateFlow<List<ShippedProduct>>(emptyList())

    val shipments:
            StateFlow<List<ShippedProduct>>
            = _shipments.asStateFlow()

    init {
        loadShipments()
    }

    private fun loadShipments() {

        val vendorId =
            authRepository.currentUserId ?: return

        println("VENDOR UID = $vendorId")

        viewModelScope.launch {

            try {

                val snapshot =
                    firestore.collection(
                        "shipped_products"
                    )
                        .get()
                        .await()

                val currentUid =
                    authRepository.currentUserId ?: ""

                val list =
                    snapshot.documents
                        .mapNotNull {

                            it.toObject(
                                ShippedProduct::class.java
                            )
                        }
                        .filter {

                            it.vendorId == currentUid ||

                                    it.customerId == currentUid
                        }
                        .sortedByDescending {
                            it.timestamp
                        }

                _shipments.value = list

            } catch (_: Exception) {

            }
        }
    }
}