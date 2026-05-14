package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.TradeRepository
import com.kutira.kone.models.TradeRequest
import com.kutira.kone.models.TradeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AcceptedTradesViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val tradeRepository: TradeRepository
) : ViewModel() {

    private val uid = authRepository.currentUserId.orEmpty()

    val acceptedTrades: StateFlow<List<TradeRequest>> =
        tradeRepository.observeAcceptedTrades(uid)
            .map { it.getOrNull().orEmpty() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun markShipped(tradeId: String) {

        viewModelScope.launch {

            tradeRepository.updateShipmentStatus(
                tradeId,
                TradeStatus.CUSTOMER_SHIPPED
            )
        }
    }

    fun completeTrade(tradeId: String) {

        viewModelScope.launch {

            tradeRepository.updateShipmentStatus(
                tradeId,
                TradeStatus.COMPLETED
            )
        }
    }

}