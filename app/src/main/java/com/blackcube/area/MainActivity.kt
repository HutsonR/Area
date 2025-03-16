package com.blackcube.area

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blackcube.core.extension.defaultPermissionRequestCode
import com.blackcube.core.navigation.Screens
import com.blackcube.home.HomeScreenRoot
import com.blackcube.tours.intro.TourIntroScreenRoot
import com.blackcube.tours.route.TourRouteScreenRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("MainActivity1", "Permission granted")
                } else {
                    Log.d("MainActivity1", "Permission denied")
                }
            }

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
                    route = Screens.TourIntroScreen.route + "/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })) { stackEntry ->
                    val itemId = Uri.decode(stackEntry.arguments?.getString("id"))
                    TourIntroScreenRoot(
                        itemId ?: "",
                        navController = navController
                    )
                }
                composable(
                    route = Screens.TourRouteScreen.route + "/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })) { stackEntry ->
                    val itemId = Uri.decode(stackEntry.arguments?.getString("id"))
                    TourRouteScreenRoot(
                        itemId ?: "",
                        navController = navController
                    )
                }
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
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
                    Toast.makeText(this, "Разрешение отклонено. Вы можете включить его в настройках.", Toast.LENGTH_LONG).show()

                    // Проверка, можно ли показать объяснение (пользователь уже отказывал раньше?)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                        Toast.makeText(this, "Это разрешение необходимо для корректной работы приложения.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Вы можете включить разрешение вручную в настройках.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else -> {
                Toast.makeText(this, "Неизвестный запрос разрешения.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}