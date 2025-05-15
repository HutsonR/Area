package com.blackcube.area

//import com.blackcube.tours.ar.ArScreen
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blackcube.area.navigation.ProtectedNavController
import com.blackcube.auth.login.LoginScreenRoot
import com.blackcube.auth.register.RegisterScreenRoot
import com.blackcube.authorization.session.SessionManager
import com.blackcube.catalog.CatalogScreenRoot
import com.blackcube.core.extension.defaultPermissionRequestCode
import com.blackcube.core.navigation.Screens
import com.blackcube.home.HomeScreenRoot
import com.blackcube.places.PlaceIntroScreenRoot
import com.blackcube.splash.SplashScreenRoot
import com.blackcube.tours.intro.TourIntroScreenRoot
import com.blackcube.tours.route.TourRouteScreenRoot
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setupWindowDecor()

        setContent {
            navController = rememberNavController()
            val navigator = remember {
                ProtectedNavController(navController, sessionManager)
            }

            val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)

            LaunchedEffect(isLoggedIn) {
                if (isLoggedIn) {
                    navController.navigate(Screens.MainScreen.route) {
                        popUpTo(0)
                    }
                } else {
                    navController.navigate(Screens.LoginScreen.route) {
                        popUpTo(0)
                    }
                }
            }

            NavigationHost(
                protectedNavController = navigator
            )
        }
    }

    private fun setupWindowDecor() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
    }

    @Composable
    private fun NavigationHost(
        protectedNavController: ProtectedNavController
    ) {
        NavHost(
            navController = navController,
            startDestination = Screens.SplashScreen.route
        ) {
            composable(Screens.SplashScreen.route) {
                SplashScreenRoot()
            }
            composable(Screens.LoginScreen.route) {
                LoginScreenRoot(
                    navController = navController
                )
            }
            composable(Screens.RegisterScreen.route) {
                RegisterScreenRoot(
                    navController = navController
                )
            }

            composable(Screens.MainScreen.route) {
                HomeScreenRoot(navController = protectedNavController)
            }
            composable(
                route = Screens.TourIntroScreen.route + "/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { stackEntry ->
                val itemId = Uri.decode(stackEntry.arguments?.getString("id"))
                TourIntroScreenRoot(
                    itemId ?: "",
                    navController = protectedNavController
                )
            }
            composable(
                route = Screens.PlaceIntroScreen.route + "/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                val itemId = Uri.decode(it.arguments?.getString("id"))
                PlaceIntroScreenRoot(
                    itemId ?: "",
                    navController = protectedNavController
                )
            }
            composable(
                route = Screens.AllCardsScreen.route + "/{cardType}",
                arguments = listOf(navArgument("cardType") { type = NavType.StringType })
            ) { stackEntry ->
                val cardType = Uri.decode(stackEntry.arguments?.getString("cardType"))
                CatalogScreenRoot(
                    cardType ?: "",
                    navController = protectedNavController
                )
            }
            composable(
                route = Screens.TourRouteScreen.route + "/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { stackEntry ->
                val itemId = Uri.decode(stackEntry.arguments?.getString("id"))
                TourRouteScreenRoot(
                    itemId ?: "",
                    navController = protectedNavController
                )
            }
            composable(Screens.ArScreen.route) {
//                    ArScreen(
//                        navController = navController
//                    )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            defaultPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Разрешение предоставлено! Теперь вы можете использовать все функции.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this,
                        "Разрешение отклонено. Вы можете включить его в настройках.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Проверка, можно ли показать объяснение (пользователь уже отказывал раньше?)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                        Toast.makeText(
                            this,
                            "Это разрешение необходимо для корректной работы приложения.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Вы можете включить разрешение вручную в настройках.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            else -> {
                Toast.makeText(this, "Неизвестный запрос разрешения.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}