package com.blackcube.core.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun safeExecute(
    context: CoroutineContext = IO,
    block: suspend () -> Unit
) = CoroutineScope(context).launch {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}