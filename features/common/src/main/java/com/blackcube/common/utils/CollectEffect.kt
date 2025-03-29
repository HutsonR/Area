package com.blackcube.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Collects a [Flow] of effects and executes a [block] for each emitted value.
 *
 * This composable is a side-effect that observes a [Flow] and triggers an action
 * when a new value is emitted. It uses [collectLatest] to ensure that only the latest
 * effect is processed, cancelling any ongoing processing of previous effects.
 * It is suitable for handling one-off events or state updates that should only be processed once.
 *
 * @param effect The [Flow] of effects to collect.
 * @param context The [CoroutineContext] to use for collecting the flow. Defaults to [EmptyCoroutineContext].
 *                This allows you to specify a dispatcher for the collection, e.g., `Dispatchers.IO`
 *                if the flow involves I/O operations. If not specified, it will be collected in the current coroutine context.
 * @param block The lambda to execute when a new effect is emitted. It receives the emitted value as a parameter.
 *
 * Example Usage:
 * ```
 * // Assuming you have a flow of String effects:
 * val myEffectFlow: Flow<String> = ...
 *
 * CollectEffect(effect = myEffectFlow) { effect ->
 *     println("Received effect: $effect")
 *     // Perform some action with the effect
 *     showToast(effect)
 * }
 * ```
 * In this example each time that a new String is emitted from `myEffectFlow`, the
 * lambda function inside `CollectEffect` will be executed.
 *
 */
@Composable
fun <T> CollectEffect(
    effect: Flow<T>,
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        effect
            .flowOn(context)
            .collectLatest {
                block(it)
            }
    }
}