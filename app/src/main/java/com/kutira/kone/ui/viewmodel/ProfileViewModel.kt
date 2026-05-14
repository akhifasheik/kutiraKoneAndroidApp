package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.local.UserPreferencesRepository
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.FabricRepository
import com.kutira.kone.models.Fabric
import com.kutira.kone.models.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val fabricRepository: FabricRepository
) : ViewModel() {

    val phone: StateFlow<String?> = authRepository.authState
        .map { it?.phoneNumber }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val role: StateFlow<UserRole?> = userPreferencesRepository.roleFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val sellerId = authRepository.currentUserId.orEmpty()

    val myListings: StateFlow<List<Fabric>> = fabricRepository.observeSellerFabrics(sellerId)
        .map { it.getOrNull().orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            userPreferencesRepository.clearRole()
            onDone()
        }
    }

    fun deleteListing(fabric: Fabric, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val uid = authRepository.currentUserId
            if (uid == null) {
                onResult("Not signed in")
                return@launch
            }
            val result = fabricRepository.deleteFabric(fabric.id, uid)
            onResult(result.exceptionOrNull()?.message)
        }
    }
}
