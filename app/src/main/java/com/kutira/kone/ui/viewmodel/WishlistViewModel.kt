package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.FabricRepository
import com.kutira.kone.data.repository.FavoritesRepository
import com.kutira.kone.models.Fabric
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class WishlistViewModel @Inject constructor(
    authRepository: AuthRepository,
    favoritesRepository: FavoritesRepository,
    fabricRepository: FabricRepository
) : ViewModel() {

    private val uid = authRepository.currentUserId.orEmpty()

    val favoriteFabrics: StateFlow<List<Fabric>> = combine(
        favoritesRepository.observeFavorites(uid),
        fabricRepository.observeFabricsRealtime()
    ) { favRes, fabRes ->
        val ids = favRes.getOrNull().orEmpty()
        val fabrics = fabRes.getOrNull().orEmpty().associateBy { it.id }
        ids.mapNotNull { fabrics[it] }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
