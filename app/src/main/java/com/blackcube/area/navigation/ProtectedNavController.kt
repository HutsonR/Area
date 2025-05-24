package com.blackcube.area.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.blackcube.authorization.api.SessionManager
import com.blackcube.core.navigation.AppNavigationController
import com.blackcube.core.navigation.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ProtectedNavController(
    private val navController: NavHostController,
    private val sessionManager: SessionManager
): AppNavigationController {

    override fun navigate(
        route: String,
        builder: NavOptionsBuilder.() -> Unit,
        argument: Pair<String, Any?>?
    ) {
        if (!sessionManager.isLoggedIn.value) {
            navController.navigate(Screens.LoginScreen.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            return
        }

        navController.navigate(route, builder)
        argument?.let { (key, value) ->
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set(key, value)
        }
    }

    override fun popBackStack(
        argument: Pair<String, Any?>?
    ): Boolean {
        argument?.let { (key, value) ->
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set(key, value)
        }
        return navController.popBackStack()
    }

    override fun <T> observeArgsAsState(
        key: String,
        initial: T?
    ): StateFlow<T?> {
        val backEntry = navController.currentBackStackEntry
        return backEntry
            ?.savedStateHandle
            ?.getStateFlow(key, initial)
            // если вдруг null, возвращаем «пустой» flow с initial
            ?: MutableStateFlow(initial)
    }

    override fun <T> removeSavedArgs(key: String): Boolean {
        val savedValue = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.remove<T>(key)

        return savedValue != null
    }
}