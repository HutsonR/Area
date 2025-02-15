package com.blackcube.area

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blackcube.core.navigation.Screens
import com.blackcube.home.HomeScreenRoot
import com.blackcube.tours.intro.TourScreenRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screens.MainScreen.route
            ) {
                composable(Screens.MainScreen.route) {
                    HomeScreenRoot(navController = navController)
                }
                composable(
                    route = Screens.TourScreen.route + "/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })) { stackEntry ->
                    val itemId = Uri.decode(stackEntry.arguments?.getString("id"))
                    TourScreenRoot(
                        itemId ?: "",
                        navController = navController
                    )
                }
            }
        }
    }
}