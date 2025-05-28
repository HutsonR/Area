package com.blackcube.profile

import androidx.lifecycle.viewModelScope
import com.blackcube.authorization.api.SessionManager
import com.blackcube.core.BaseViewModel
import com.blackcube.profile.store.ProfileEffect
import com.blackcube.profile.store.ProfileIntent
import com.blackcube.profile.store.ProfileState
import com.blackcube.remote.repository.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sessionManager: SessionManager
) : BaseViewModel<ProfileState, ProfileEffect>(ProfileState()) {

    init {
        viewModelScope.launch {
            try {
                modifyState { copy(isLoading = true) }
                profileRepository.getStats().let {
                    modifyState { copy(stats = it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                modifyState { copy(isLoading = false) }
            }
        }
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.OnLogout -> logout()
            ProfileIntent.GoBack -> effect(ProfileEffect.NavigateToBack)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            sessionManager.clearToken()
        }
    }
}