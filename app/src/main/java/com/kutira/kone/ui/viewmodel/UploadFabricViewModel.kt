package com.kutira.kone.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.FabricRepository
import com.kutira.kone.data.repository.LocationRepository
import com.kutira.kone.models.Fabric
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UploadUiState(
    val saving: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class UploadFabricViewModel @Inject constructor(
    private val fabricRepository: FabricRepository,
    private val authRepository: AuthRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(UploadUiState())
    val ui: StateFlow<UploadUiState> = _ui.asStateFlow()

    fun upload(
        imageUri: Uri?,
        materialType: String,
        color: String,
        size: String,
        description: String,
        price: Double,
        swapAvailable: Boolean,
        onDone: () -> Unit
    ) {
        if (imageUri == null) {
            _ui.value = _ui.value.copy(error = "Please add a fabric photo before uploading.")
            return
        }
        val uid = authRepository.currentUserId
        val phone = authRepository.currentPhone.orEmpty()
        if (uid == null) {
            _ui.value = _ui.value.copy(error = "You must be signed in.")
            return
        }
        viewModelScope.launch {
            _ui.value = UploadUiState(saving = true, error = null, success = false)
            try {
                val loc = locationRepository.getCurrentLocationOrNull()
                    ?: throw IllegalStateException("Location is required. Enable GPS and try again.")
                val url = fabricRepository.uploadFabricImage(uid, imageUri)
                val fabric = Fabric(
                    imageUrl = url,
                    materialType = materialType.trim(),
                    color = color.trim(),
                    size = size.trim(),
                    description = description.trim(),
                    sellerId = uid,
                    sellerPhone = phone,
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    available = true,
                    price = price,
                    swapAvailable = swapAvailable
                )
                fabricRepository.createFabric(fabric).getOrThrow()
                _ui.value = UploadUiState(saving = false, success = true)
                onDone()
            } catch (e: Exception) {
                _ui.value = UploadUiState(saving = false, error = e.message ?: "Upload failed")
            }
        }
    }

    fun resetMessage() {
        _ui.value = _ui.value.copy(error = null, success = false)
    }
}
