package com.blackcube.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.blackcube.auth.R
import com.blackcube.auth.login.store.models.LoginEffect
import com.blackcube.auth.login.store.models.LoginIntent
import com.blackcube.auth.login.store.models.LoginState
import com.blackcube.common.ui.AlertData
import com.blackcube.common.ui.CustomActionButton
import com.blackcube.common.ui.CustomTextInput
import com.blackcube.common.ui.ShowAlertDialog
import com.blackcube.common.ui.ShowProgressBackgroundIndicator
import com.blackcube.common.utils.CollectEffect
import com.blackcube.core.navigation.Screens
import kotlinx.coroutines.flow.Flow

@Composable
fun LoginScreenRoot(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect

    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(
            navController = navController,
            state = state,
            effects = effects,
            onIntent = viewModel::handleIntent
        )

        ShowProgressBackgroundIndicator(state.isLoading)
    }
}

@Composable
fun LoginScreen(
    navController: NavController,
    state: LoginState,
    effects: Flow<LoginEffect>,
    onIntent: (LoginIntent) -> Unit
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    CollectEffect(effects) { effect ->
        when (effect) {
            LoginEffect.NavigateToRegister -> navController.navigate(Screens.RegisterScreen.route)
            is LoginEffect.ShowAlert -> isAlertVisible = true
            LoginEffect.Success -> navController.navigate(Screens.MainScreen.route)
        }
    }

    if (isAlertVisible) {
        ShowAlertDialog(
            alertData = AlertData(
                message = R.string.alert_login_message
            ),
            onActionButtonClick = {
                isAlertVisible = false
            }
        )
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.main_background))
            .padding(vertical = 60.dp, horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.login_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.title_color),
            textAlign = TextAlign.Center
        )
        EmailInput(state.email, state.emailErrorRes) {
            onIntent(LoginIntent.OnEmailChange(it))
        }
        PasswordInput(state.password, state.passwordErrorRes) {
            onIntent(LoginIntent.OnPasswordChange(it))
        }
        CustomActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            text = stringResource(id = R.string.login_login_button),
            onClick = { onIntent(LoginIntent.OnLoginClick) },
            backgroundColor = colorResource(R.color.black),
            textColor = colorResource(R.color.white),
            isActive = state.isActionButtonActive
        )
        CustomActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            text = stringResource(id = R.string.login_register_button),
            onClick = { onIntent(LoginIntent.OnRegisterClick) },
            backgroundColor = colorResource(R.color.main_background),
            textColor = colorResource(R.color.title_color)
        )
    }
}

@Composable
fun EmailInput(
    value: String,
    error: Int?,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    CustomTextInput(
        modifier = Modifier.padding(top = 24.dp),
        value = value,
        onValueChange = onValueChange,
        label = stringResource(id = R.string.login_email_label),
        placeholder = stringResource(id = R.string.login_email_placeholder),
        errorText = error?.let { context.getString(it) },
    )
}

@Composable
fun PasswordInput(
    value: String,
    error: Int?,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }

    CustomTextInput(
        modifier = Modifier.padding(top = 12.dp),
        value = value,
        onValueChange = onValueChange,
        label = stringResource(id = R.string.login_password_label),
        placeholder = stringResource(id = R.string.login_password_placeholder),
        errorText = error?.let { context.getString(it) },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val icon = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { visible = !visible }) {
                Icon(imageVector = icon, contentDescription = null)
            }
        }
    )
}