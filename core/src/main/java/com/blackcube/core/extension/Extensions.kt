package com.blackcube.core.extension

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

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