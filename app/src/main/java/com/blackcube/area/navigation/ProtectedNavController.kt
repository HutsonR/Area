package com.blackcube.area.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.blackcube.authorization.session.SessionManager
import com.blackcube.core.navigation.AppNavigationController
import com.blackcube.core.navigation.Screens

internal class ProtectedNavController(
    private val navController: NavHostController,
    private val sessionManager: SessionManager
): AppNavigationController {

    override fun navigate(
        route: String,
        builder: NavOptionsBuilder.() -> Unit
    ) {
        if (sessionManager.isLoggedIn.value) {
            navController.navigate(route, builder)
        } else {
            navController.navigate(Screens.LoginScreen.route) {
                popUpTo(0)
            }
        }
    }

    override fun popBackStack() = navController.popBackStack()
}