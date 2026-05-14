package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.FabricRepository
import com.kutira.kone.models.Fabric
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VendorDashboardState(
    val fabrics: List<Fabric> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class VendorDashboardViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val fabricRepository: FabricRepository
) : ViewModel() {

    private val sellerId = authRepository.currentUserId.orEmpty()

    val state: StateFlow<VendorDashboardState> =
        fabricRepository.observeSellerFabrics(sellerId)
            .map { result ->
                VendorDashboardState(
                    fabrics = result.getOrNull().orEmpty(),
                    error = result.exceptionOrNull()?.message
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VendorDashboardState())

    fun toggleAvailability(fabric: Fabric) {
        viewModelScope.launch {
            fabricRepository.setAvailability(fabric.id, !fabric.available)
        }
    }
}
