package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.FabricRepository
import com.kutira.kone.data.repository.FavoritesRepository
import com.kutira.kone.data.repository.GeminiRepository
import com.kutira.kone.data.repository.LocationRepository
import com.kutira.kone.data.repository.TradeRepository
import com.kutira.kone.models.Fabric
import com.kutira.kone.utils.DistanceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class FabricDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fabricRepository: FabricRepository,
    private val locationRepository: LocationRepository,
    private val tradeRepository: TradeRepository,
    private val geminiRepository: GeminiRepository,
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val fabricId: String = checkNotNull(savedStateHandle["fabricId"])

    private val _fabric = MutableStateFlow<Fabric?>(null)
    val fabric: StateFlow<Fabric?> = _fabric.asStateFlow()

    private val _distanceLabel = MutableStateFlow<String?>(null)
    val distanceLabel: StateFlow<String?> = _distanceLabel.asStateFlow()

    private val _tradeMessage = MutableStateFlow<String?>(null)
    val tradeMessage: StateFlow<String?> = _tradeMessage.asStateFlow()

    private val _aiResult = MutableStateFlow<String?>(null)
    val aiResult: StateFlow<String?> = _aiResult.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _favorite = MutableStateFlow(false)
    val favorite: StateFlow<Boolean> = _favorite.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val f = fabricRepository.getFabric(fabricId)
            _fabric.value = f
            val loc = locationRepository.getCurrentLocationOrNull()
            if (f != null && loc != null) {
                val d = DistanceUtils.distanceKm(
                    loc.latitude,
                    loc.longitude,
                    f.latitude,
                    f.longitude
                )
                _distanceLabel.value = DistanceUtils.formatDistance(d)
            }
            val uid = authRepository.currentUserId
            if (uid != null && f != null) {
                _favorite.value = favoritesRepository.isFavorite(uid, f.id)
            }
        }
    }

    fun requestTrade(
        offeredItem: String,
        offeredDescription: String,
        offeredSize: String,
        offeredColor: String,
        tradeMessage: String
    ) {
        val f = _fabric.value ?: return
        val uid = authRepository.currentUserId ?: return
        val senderPhone = authRepository.currentPhone ?: ""
        
        if (uid == f.sellerId) {
            _tradeMessage.value = "You cannot trade your own listing."
            return
        }
        
        viewModelScope.launch {
            val result = tradeRepository.createTradeRequest(
                senderId = uid,
                senderPhone = senderPhone,
                receiverId = f.sellerId,
                fabricId = f.id,
                fabricTitle = "${f.color} ${f.materialType}".trim(),
                offeredItem = offeredItem,
                offeredDescription = offeredDescription,
                offeredSize = offeredSize,
                offeredColor = offeredColor,
                tradeMessage = tradeMessage
            )
            _tradeMessage.value = result.fold(
                onSuccess = { "Trade request sent." },
                onFailure = { it.message ?: "Unable to send request" }
            )
        }
    }

    fun buyScrap() {
        val f = _fabric.value ?: return
        val uid = authRepository.currentUserId ?: return
        if (uid == f.sellerId) {
            _tradeMessage.value = "This is your listing."
            return
        }
        _tradeMessage.value =
            "Purchase flow: contact the seller at ${f.sellerPhone}. In production, integrate payments here."
    }

    fun generateIdeas() {
        val f = _fabric.value ?: return
        viewModelScope.launch {
            _aiLoading.value = true
            _aiResult.value = null
            val text = geminiRepository.generateDesignIdeas(
                materialType = f.materialType,
                color = f.color,
                size = f.size
            ).getOrElse { e ->
                _aiLoading.value = false
                _aiResult.value = e.message
                return@launch
            }
            _aiResult.value = text
            _aiLoading.value = false
        }
    }

    fun toggleFavorite() {
        val f = _fabric.value ?: return
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(uid, f.id, _favorite.value)
            _favorite.value = !_favorite.value
        }
    }

    fun clearTradeMessage() {
        _tradeMessage.value = null
    }
}
