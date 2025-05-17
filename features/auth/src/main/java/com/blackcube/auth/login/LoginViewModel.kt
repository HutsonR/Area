package com.blackcube.auth.login

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.blackcube.auth.R
import com.blackcube.auth.domain.AuthUseCase
import com.blackcube.auth.login.store.models.LoginEffect
import com.blackcube.auth.login.store.models.LoginIntent
import com.blackcube.auth.login.store.models.LoginState
import com.blackcube.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
): BaseViewModel<LoginState, LoginEffect>(LoginState()) {

    fun handleIntent(loginIntent: LoginIntent) {
        when(loginIntent) {
            LoginIntent.OnLoginClick -> onLoginClick()
            LoginIntent.OnRegisterClick -> onRegisterClick()
            is LoginIntent.OnEmailChange -> onEmailChange(loginIntent.email)
            is LoginIntent.OnPasswordChange -> onPasswordChange(loginIntent.password)
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
                && getState().password.isNotBlank()
                && getState().emailErrorRes == null
                && getState().passwordErrorRes == null

        modifyState { copy(isActionButtonActive = buttonEnabled) }
    }

    private fun onLoginClick() {
        viewModelScope.launch {
            modifyState { copy(isLoading = true) }
            try {
                val isSuccessAuth = authUseCase.login(
                    email = getState().email,
                    password = getState().password
                )
                if (isSuccessAuth) {
                    effect(LoginEffect.Success)
                } else {
                    effect(LoginEffect.ShowAlert)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                effect(LoginEffect.ShowAlert)
            } finally {
                modifyState { copy(isLoading = false) }
            }
        }
    }

    private fun onRegisterClick() = effect(LoginEffect.NavigateToRegister)

}