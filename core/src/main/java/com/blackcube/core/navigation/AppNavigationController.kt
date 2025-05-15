package com.blackcube.core.navigation

import androidx.navigation.NavOptionsBuilder

interface AppNavigationController {
    /**
     * Навигация к маршруту [route], только если залогинен.
     * Иначе — отправка на LoginScreen с очисткой стека.
     */
    fun navigate(
        route: String,
        builder: NavOptionsBuilder.() -> Unit = {}
    )

    fun popBackStack(): Boolean
}