package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.repository.FabricRepository
import com.kutira.kone.data.repository.LocationRepository
import com.kutira.kone.models.Fabric
import com.kutira.kone.utils.DistanceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FabricItem(
    val fabric: Fabric,
    val distanceKm: Double?
)

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val fabricRepository: FabricRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val materials = listOf("All", "Silk", "Cotton", "Wool", "Linen", "Denim")

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _material = MutableStateFlow("All")
    val material: StateFlow<String> = _material

    private val _radiusKm = MutableStateFlow(5.0)
    val radiusKm: StateFlow<Double> = _radiusKm

    private val _userLatLng = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLatLng: StateFlow<Pair<Double, Double>?> = _userLatLng

    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: StateFlow<String?> = _locationError

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing

    private val fabrics = fabricRepository.observeFabricsRealtime()

    val items: StateFlow<List<FabricItem>> = combine(
        fabrics,
        _query,
        _material,
        _radiusKm,
        _userLatLng
    ) { result, q, mat, radius, latLng ->
        val list = result.getOrNull().orEmpty().filter { it.available }
        val filtered = list.filter { fabric ->
            val text = "${fabric.materialType} ${fabric.color} ${fabric.description}".lowercase()
            val matchesQuery = q.isBlank() || text.contains(q.lowercase())
            val matchesMaterial = mat == "All" || fabric.materialType.equals(mat, ignoreCase = true)
            matchesQuery && matchesMaterial
        }.mapNotNull { fabric ->
            val loc = latLng
            if (loc == null) {
                FabricItem(fabric, null)
            } else {
                val d = DistanceUtils.distanceKm(
                    loc.first,
                    loc.second,
                    fabric.latitude,
                    fabric.longitude
                )
                if (d <= radius) FabricItem(fabric, d) else null
            }
        }.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val loadError: StateFlow<String?> = fabrics
        .map { it.exceptionOrNull()?.message }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        refreshLocation()
    }

    fun refreshLocation() {
        viewModelScope.launch {
            _locationError.value = null
            val loc = locationRepository.getCurrentLocationOrNull()
            if (loc == null) {
                _locationError.value = "Location unavailable. Enable GPS and grant permission to sort by distance."
                _userLatLng.value = null
            } else {
                _userLatLng.value = loc.latitude to loc.longitude
            }
        }
    }

    fun setQuery(value: String) {
        _query.value = value
    }

    fun setMaterial(value: String) {
        _material.value = value
    }

    fun setRadiusKm(km: Double) {
        _radiusKm.value = km
    }

    fun refreshFabrics() {
        viewModelScope.launch {
            _refreshing.value = true
            refreshLocation()
            _refreshing.value = false
        }
    }
}
