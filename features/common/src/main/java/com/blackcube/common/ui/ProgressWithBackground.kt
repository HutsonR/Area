package com.blackcube.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput


/**
 * @param isLoadingData if true circular progress bar will show.
 * Same composable used in all screens.
 */
@Composable
fun ShowProgressBackgroundIndicator(
    isLoadingData: Boolean
) {
    if (isLoadingData) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                // перехват всех касаний, чтобы ничего не проходило вниз
                .pointerInput(Unit) {
                    detectTapGestures {  }
                }
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}