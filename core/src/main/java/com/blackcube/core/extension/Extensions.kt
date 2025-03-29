package com.blackcube.core.extension

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

const val defaultPermissionRequestCode = 100

/**
 * Executes a suspending function within a given coroutine context and safely handles exceptions.
 *
 * This function provides a convenient way to execute suspending code blocks, especially those
 * that might throw exceptions. It wraps the execution in a try-catch block and returns null
 * if any exception occurs during the execution of the provided [block]. The exception is also
 * printed to the standard error stream for debugging purposes.
 *
 * @param T The type of the result returned by the [block].
 * @param context The coroutine context in which the [block] should be executed. Defaults to [Dispatchers.IO].
 * @param block The suspending function to be executed.
 * @return The result of executing the [block] if successful, or null if any exception occurred.
 *
 * @throws Exception if any exception occurs during execution of the [block], it will be caught and `null` will be returned.
 *                   The stack trace will be printed to stderr.
 *
 * Example Usage:
 * ```kotlin
 * suspend fun fetchData(): String {
 *     // Simulate a network request that might fail
 *     delay(1000)
 *     if (Random.nextBoolean()) {
 *         return "Data successfully fetched"
 *     } else {
 *         throw IOException("Network error")
 *     }
 * }
 *
 * suspend fun main() {
 *     val result = safeExecute { fetchData() }
 *     if (result != null) {
 *         println("Result: $result")
 *     } else {
 *         println("Failed to fetch data")
 *     }
 * }
 * ```
 */
suspend fun <T> safeExecute(
    context: CoroutineContext = Dispatchers.IO,
    block: suspend () -> T
): T? = try {
    withContext(context) {
        block()
    }
} catch (e: Exception) {
    e.printStackTrace()
    null
}

/**
 * Checks if a specific permission is granted and handles the permission request flow.
 *
 * This function simplifies the process of checking and requesting runtime permissions on Android.
 * It handles three main scenarios:
 * 1. **Permission Granted:** If the permission is already granted, the `onGranted` callback is executed.
 * 2. **Show Rationale:** If the user has previously denied the permission and the system suggests
 *    showing a rationale, the `showInContextUI` callback is executed. This allows you to display
 *    an explanation to the user about why the permission is needed.
 * 3. **Request Permission:** If the permission is not granted and a rationale is not required,
 *    the permission is directly requested from the user.
 *
 * @param activity The current activity.
 * @param permission The permission string to check (e.g., `Manifest.permission.CAMERA`).
 * @param requestCode An optional request code to use when requesting the permission.
 *                    This code will be returned in the `onRequestPermissionsResult` callback of the Activity.
 *                    If not provided it will use the default request code 0.
 * @param showInContextUI A lambda function to be executed when the system suggests showing a rationale for the permission.
 *                        This should display a UI element explaining why the permission is needed.
 * @param onGranted A lambda function to be executed when the permission is already granted.
 *
 * @see ContextCompat.checkSelfPermission
 * @see ActivityCompat.shouldShowRequestPermissionRationale
 * @see ActivityCompat.requestPermissions
 */
fun checkPermission(
    activity: Activity,
    permission: String,
    requestCode: Int? = null,
    showInContextUI: () -> Unit,
    onGranted: () -> Unit
) {
    when {
        ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED -> {
            onGranted()
        }

        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
            showInContextUI()
        }

        else -> {
            requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }
}

/**
 * Requests permissions from the user.
 *
 * This function simplifies the process of requesting permissions by providing a convenient way to
 * handle the optional request code. If no request code is provided, a default one is used.
 * It delegates the actual permission request to [ActivityCompat.requestPermissions].
 *
 * @param activity The activity that is requesting permissions.
 * @param permissions An array of permissions to request. These should be Android permission strings,
 *                    e.g., `android.Manifest.permission.CAMERA`.
 * @param requestCode An optional integer request code that will be passed back in the
 *                    `onRequestPermissionsResult` callback. If not provided, a default request
 *                    code [defaultPermissionRequestCode] will be used.
 *
 * @see ActivityCompat.requestPermissions
 * @see defaultPermissionRequestCode
 */
fun requestPermissions(
    activity: Activity,
    permissions: Array<String>,
    requestCode: Int? = null
) {
    requestPermissions(activity, permissions, requestCode ?: defaultPermissionRequestCode)
}