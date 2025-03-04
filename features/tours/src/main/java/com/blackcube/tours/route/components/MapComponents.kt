package com.blackcube.tours.route.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcube.tours.R

//@Composable
//fun MapControlButton(
//    modifier: Modifier = Modifier,
//    icon: ImageVector,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = modifier
//            .size(48.dp)
//            .clickable { onClick.invoke() },
//        shape = RoundedCornerShape(10.dp),
//        elevation = CardDefaults.cardElevation(8.dp),
//        colors = CardDefaults.cardColors(containerColor = colorResource(com.blackcube.common.R.color.white).copy(alpha = 0.9f))
//    ) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = icon,
//                contentDescription = "Map Control Icon",
//                tint = colorResource(id = com.blackcube.common.R.color.black),
//                modifier = Modifier
//                    .size(32.dp)
//            )
//        }
//    }
//}

@Composable
fun MapBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable { onClick.invoke() },
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(com.blackcube.common.R.color.white))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Map Control Icon",
                tint = colorResource(id = com.blackcube.common.R.color.black),
                modifier = Modifier
                    .size(26.dp)
            )
        }
    }
}

@Composable
fun MapArButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick.invoke() }
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        colorResource(com.blackcube.common.R.color.ar_start_color),
                        colorResource(com.blackcube.common.R.color.ar_end_color)
                    )
                )
            )
            .padding(
                vertical = 10.dp,
                horizontal = 18.dp
            )
            .clip(RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ar_icon),
            contentDescription = "AR",
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = R.string.ar_mode),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

//@Composable
//@Preview
//fun MapPlusButtonPreview() {
//    Box(modifier = Modifier.size(100.dp).background(Color.White), contentAlignment = Alignment.Center) {
//        MapControlButton(icon = Icons.Default.Add) {}
//    }
//}
//
//@Composable
//@Preview
//fun MapMinusButtonPreview() {
//    MapControlButton(icon = Icons.Default.Remove) {}
//}

@Composable
@Preview
fun MapBackButtonPreview() {
    MapBackButton {}
}

@Composable
@Preview
fun MapArButtonPreview() {
    MapArButton {}
}