package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.local.UserPreferencesRepository
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.models.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SplashDestination {
    data object Onboarding : SplashDestination
    data object Role : SplashDestination
    data object Auth : SplashDestination
    data class Home(val role: UserRole) : SplashDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    fun resolve() {
        viewModelScope.launch {
            val onboardingDone = userPreferencesRepository.onboardingDoneFlow.first()
            if (!onboardingDone) {
                _destination.value = SplashDestination.Onboarding
                return@launch
            }
            val role = userPreferencesRepository.roleFlow.first()
            if (role == null) {
                _destination.value = SplashDestination.Role
                return@launch
            }
            val user = authRepository.authState.value
            if (user == null) {
                _destination.value = SplashDestination.Auth
                return@launch
            }
            _destination.value = SplashDestination.Home(role)
        }
    }
}
