package com.kutira.kone.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthProvider
import com.kutira.kone.data.local.UserPreferencesRepository
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.messaging.FirebaseMessaging

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    private var verificationId: String? = null

    fun sendOtp(phoneE164: String, activity: Activity) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            val callbacks = authRepository.verificationCallbacks(
                onCodeSent = { id ->
                    verificationId = id
                    _ui.value = _ui.value.copy(loading = false, otpSent = true)
                },
                onError = { e ->
                    _ui.value = _ui.value.copy(loading = false, error = e.message ?: "Verification failed")
                }
            )
            val options = authRepository.buildPhoneAuthOptions(phoneE164, activity, callbacks)
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    fun verifyOtp(code: String, onSuccess: () -> Unit) {
        val id = verificationId
        if (id.isNullOrBlank()) {
            _ui.value = _ui.value.copy(error = "Request OTP first")
            return
        }
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            try {
                val credential = PhoneAuthProvider.getCredential(id, code)
                val user = authRepository.signInWithCredential(credential).getOrThrow()
                val role = userPreferencesRepository.roleFlow.first()
                userRepository.upsertProfile(user.uid, user.phoneNumber, role?.name)
                FirebaseMessagingBridge.refreshToken(user.uid, userRepository)
                _ui.value = _ui.value.copy(loading = false)
                onSuccess()
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(loading = false, error = e.message ?: "Invalid code")
            }
        }
    }

    fun clearError() {
        _ui.value = _ui.value.copy(error = null)
    }
}

data class AuthUiState(
    val loading: Boolean = false,
    val otpSent: Boolean = false,
    val error: String? = null
)

/**
 * Bridges FCM token refresh without pulling FirebaseMessaging into the ViewModel constructor.
 */
object FirebaseMessagingBridge {
    suspend fun refreshToken(uid: String, userRepository: UserRepository) {
        val token = FirebaseMessaging.getInstance().token.await()
        userRepository.updateFcmToken(uid, token)
    }
}
