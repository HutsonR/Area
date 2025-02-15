package com.blackcube.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


/**
 * @param isLoadingData if true circular progress bar will show.
 * Same composable used in all screens.
 */
@Composable
fun ShowProgressIndicator(
    isLoadingData: Boolean
) {
    if (isLoadingData) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}