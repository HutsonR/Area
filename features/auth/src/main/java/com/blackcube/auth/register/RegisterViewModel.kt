package com.blackcube.auth.register

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.blackcube.auth.R
import com.blackcube.auth.domain.AuthUseCase
import com.blackcube.auth.register.store.models.RegisterEffect
import com.blackcube.auth.register.store.models.RegisterIntent
import com.blackcube.auth.register.store.models.RegisterState
import com.blackcube.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
): BaseViewModel<RegisterState, RegisterEffect>(RegisterState()) {

    fun handleIntent(loginIntent: RegisterIntent) {
        when(loginIntent) {
            RegisterIntent.OnLoginClick -> onLoginClick()
            RegisterIntent.OnRegisterClick -> onRegisterClick()
            is RegisterIntent.OnEmailChange -> onEmailChange(loginIntent.email)
            is RegisterIntent.OnNameChange -> onNameChange(loginIntent.name)
            is RegisterIntent.OnPasswordChange -> onPasswordChange(loginIntent.password)
        }
    }

    private fun onEmailChange(email: String) {
        val error = when {
            email.isBlank() -> R.string.error_empty
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.error_wrong_email
            else -> null
        }

        modifyState {
            copy(
                email = email,
                emailErrorRes = error
            )
        }
        checkActionButtonState()
    }

    private fun onNameChange(name: String) {
        val error = when {
            name.isBlank() -> R.string.error_empty
            name.length < 3 -> R.string.error_wrong_name
            else -> null
        }

        modifyState {
            copy(
                name = name,
                nameErrorRes = error
            )
        }
        checkActionButtonState()
    }

    private fun onPasswordChange(password: String) {
        val error = when {
            password.length < 8 -> R.string.error_wrong_password_length
            !password.any { it.isDigit() } -> R.string.error_wrong_password_digit
            !password.any { it.isUpperCase() } -> R.string.error_wrong_password_uppercase
            else -> null
        }

        modifyState {
            copy(
                password = password,
                passwordErrorRes = error
            )
        }
        checkActionButtonState()
    }

    private fun checkActionButtonState() {
        val buttonEnabled = getState().email.isNotBlank()
                && getState().name.isNotBlank()
                && getState().password.isNotBlank()
                && getState().emailErrorRes == null
                && getState().nameErrorRes == null
                && getState().passwordErrorRes == null

        modifyState { copy(isActionButtonActive = buttonEnabled) }
    }

    private fun onRegisterClick() {
        viewModelScope.launch {
            modifyState { copy(isLoading = true) }
            try {
                val isSuccessRegister = authUseCase.register(
                    email = getState().email,
                    name = getState().name,
                    password = getState().password
                )
                if (isSuccessRegister) {
                    effect(RegisterEffect.Success)
                } else {
                    effect(RegisterEffect.ShowAlert)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                effect(RegisterEffect.ShowAlert)
            } finally {
                modifyState { copy(isLoading = false) }
            }
        }
    }

    private fun onLoginClick() = effect(RegisterEffect.NavigateToLogin)

}