package com.blackcube.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource

@Composable
fun SplashScreenRoot() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(com.blackcube.common.R.color.main_background))
    ) { }
}