package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.TradeRepository
import com.kutira.kone.models.TradeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CustomerTradesViewModel @Inject constructor(
    authRepository: AuthRepository,
    tradeRepository: TradeRepository
) : ViewModel() {

    private val uid = authRepository.currentUserId.orEmpty()

    val requests: StateFlow<List<TradeRequest>> =
        tradeRepository.observeOutgoingFor(uid)
            .map { it.getOrNull().orEmpty() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}