package com.blackcube.common.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier
) {
    Text(
        text = text,
        fontSize = 21.sp,
        fontWeight = FontWeight.Bold,
        color = colorResource(com.blackcube.common.R.color.title_color),
        modifier = modifier
    )
}