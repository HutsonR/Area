package com.blackcube.places

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.blackcube.common.ui.CustomActionButtonWithIcon
import com.blackcube.common.ui.ShowAlertDialog
import com.blackcube.common.ui.ShowProgressIndicator
import com.blackcube.common.utils.CollectEffect
import com.blackcube.common.utils.map.MapUtil.navigateToMap
import com.blackcube.core.navigation.AppNavigationController
import com.blackcube.places.store.models.PlaceIntroEffect
import com.blackcube.places.store.models.PlaceIntroIntent
import com.blackcube.places.store.models.PlaceIntroState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

@Composable
fun PlaceIntroScreenRoot(
    placeId: String,
    navController: AppNavigationController,
    viewModel: PlaceIntroViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect

    LaunchedEffect(placeId) {
        viewModel.fetchPlace(placeId)
    }

    PlaceIntroScreen(
        navController = navController,
        state = state,
        effects = effects,
        onIntent = viewModel::handleIntent
    )
}

@Composable
fun PlaceIntroScreen(
    navController: AppNavigationController,
    state: PlaceIntroState,
    effects: Flow<PlaceIntroEffect>,
    onIntent: (PlaceIntroIntent) -> Unit
) {
    val context = LocalContext.current
    var isAlertVisible by remember { mutableStateOf(false) }

    CollectEffect(effects) { effect ->
        when (effect) {
            PlaceIntroEffect.NavigateToBack -> navController.popBackStack()

            is PlaceIntroEffect.ShowAlert -> {
                isAlertVisible = true
            }

            is PlaceIntroEffect.ShowMap -> navigateToMap(
                request = effect.request,
                context = context
            )
        }
    }

    if (isAlertVisible) {
        ShowAlertDialog(
            onActionButtonClick = {
                isAlertVisible = false
                onIntent(PlaceIntroIntent.OnBackClick)
            }
        )
    }

    if (state.isLoading) {
        BackButton(onBackClick = { onIntent(PlaceIntroIntent.OnBackClick) })
        ShowProgressIndicator(state.isLoading)
    }

    val placeModel = state.placeModel ?: return // Показ алерта при null в VM

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(com.blackcube.common.R.color.main_background))
    ) {
        LazyColumn {
            item {
                Image(imageUrl = placeModel.imageUrl)
            }
            item {  Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    text = placeModel.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = colorResource(id = com.blackcube.common.R.color.title_color)
                )
            }
            item {  Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    text = placeModel.description,
                    fontSize = 16.sp,
                    color = colorResource(id = com.blackcube.common.R.color.title_color)
                )
            }
            item {  Spacer(modifier = Modifier.height(32.dp)) }
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    CustomActionButtonWithIcon(
                        backgroundColor = colorResource(id = com.blackcube.common.R.color.button_background_dark_gray),
                        textColor = colorResource(id = com.blackcube.common.R.color.black),
                        iconColor = colorResource(id = com.blackcube.common.R.color.black),
                        text = stringResource(id = com.blackcube.common.R.string.show_in_map),
                        icon = Icons.Default.Map,
                        onClick = { onIntent(PlaceIntroIntent.OnShowMapClick) }
                    )
                }
            }
        }
        BackButton(onBackClick = { onIntent(PlaceIntroIntent.OnBackClick) })
    }
}

@Preview
@Composable
fun PreviewIntroScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(com.blackcube.common.R.color.main_background))
            .padding(vertical = 24.dp)
    ) {
        Image(
            imageUrl = "https://i.pinimg.com/originals/29/36/06/2936068fffd819ba4c2abeaf7dd04206.png"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "title",
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            color = colorResource(id = com.blackcube.common.R.color.title_color)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "description",
            fontSize = 16.sp,
            color = colorResource(id = com.blackcube.common.R.color.title_color)
        )

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
fun BackButton(
    onBackClick: () -> Unit
) {
    IconButton(
        onClick = { onBackClick.invoke() },
        modifier = Modifier
            .padding(start = 20.dp, top = 40.dp)
            .shadow(8.dp, shape = CircleShape)
            .background(Color.White, shape = CircleShape)
            .size(42.dp)
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.Black
        )
    }
}

@Composable
fun Image(
    imageUrl: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        val context = LocalContext.current
        val placeholder = com.blackcube.common.R.drawable.placeholder

        val imageRequest = ImageRequest.Builder(context)
            .data(imageUrl)
            .dispatcher(Dispatchers.IO)
            .memoryCacheKey(imageUrl)
            .diskCacheKey(imageUrl)
            .placeholder(placeholder)
            .error(placeholder)
            .fallback(placeholder)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .build()

        // Load and display the image with AsyncImage
        AsyncImage(
            model = imageRequest,
            contentDescription = "image",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop,
        )
    }
}