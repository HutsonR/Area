package com.blackcube.core.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun safeExecute(
    context: CoroutineContext = IO,
    scope: CoroutineScope = CoroutineScope(context),
    block: () -> Unit
) = scope.launch(context) {
    try {

    } catch (e: Exception) {
        e.printStackTrace()
    }
}