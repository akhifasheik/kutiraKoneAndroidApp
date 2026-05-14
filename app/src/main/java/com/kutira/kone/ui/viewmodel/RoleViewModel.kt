package com.kutira.kone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutira.kone.data.local.UserPreferencesRepository
import com.kutira.kone.data.repository.AuthRepository
import com.kutira.kone.data.repository.UserRepository
import com.kutira.kone.models.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class RoleViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun selectRole(role: UserRole, onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferencesRepository.setRole(role)
            val uid = authRepository.currentUserId
            val phone = authRepository.currentPhone
            if (uid != null) {
                userRepository.upsertProfile(uid, phone, role.name)
            }
            onDone()
        }
    }
}
