package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.FabricRepository
import com.kutira.kone.data.repository.TradeRepository
import com.kutira.kone.models.Fabric
import com.kutira.kone.models.TradeRequest
import com.kutira.kone.models.TradeStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

data class TradeUiModel(
    val request: TradeRequest,
    val fabric: Fabric?
)

@HiltViewModel
class TradeRequestsViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val tradeRepository: TradeRepository,
    private val fabricRepository: FabricRepository
) : ViewModel() {

    private val uid = authRepository.currentUserId.orEmpty().also {
        android.util.Log.d("TRADE_DEBUG", "Vendor UID = $it")
    }

    private val incoming = tradeRepository.observeIncomingFor(uid)
    private val fabrics = fabricRepository.observeFabricsRealtime()

    val incomingModels: StateFlow<List<TradeUiModel>> = combine(incoming, fabrics) { reqRes, fabRes ->
        val requests = reqRes.getOrNull().orEmpty()
        val fabricsMap = fabRes.getOrNull().orEmpty().associateBy { it.id }
        requests.map { TradeUiModel(it, fabricsMap[it.fabricId]) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val pendingBadge: StateFlow<Int> = tradeRepository.pendingCountForReceiver(uid)
        .map { it.getOrNull() ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun accept(request: TradeRequest) {
        viewModelScope.launch {
            val id = request.id.ifBlank { request.requestId }
            tradeRepository.updateStatus(id, uid, TradeStatus.ACCEPTED)
            _message.value = "Request accepted"
        }
    }

    fun reject(request: TradeRequest) {
        viewModelScope.launch {
            val id = request.id.ifBlank { request.requestId }
            tradeRepository.updateStatus(id, uid, TradeStatus.REJECTED)
            _message.value = "Request rejected"
        }
    }

    fun markVendorShipped(
        trade: TradeRequest
    ) {

        viewModelScope.launch {

            tradeRepository.markVendorShipped(trade)
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
