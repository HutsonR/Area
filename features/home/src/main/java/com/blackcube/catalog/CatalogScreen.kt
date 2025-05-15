package com.blackcube.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.blackcube.catalog.models.CatalogItem
import com.blackcube.catalog.models.CatalogType
import com.blackcube.catalog.store.models.CatalogEffect
import com.blackcube.catalog.store.models.CatalogIntent
import com.blackcube.catalog.store.models.CatalogState
import com.blackcube.common.ui.ShowAlertDialog
import com.blackcube.common.ui.ShowProgressIndicator
import com.blackcube.common.utils.CollectEffect
import com.blackcube.core.navigation.AppNavigationController
import com.blackcube.core.navigation.Screens
import com.blackcube.home.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

@Composable
fun CatalogScreenRoot(
    catalogType: String,
    navController: AppNavigationController,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect

    val contentType = try {
        CatalogType.valueOf(catalogType)
    } catch (e: IllegalArgumentException) {
        CatalogType.TOURS
    }

    LaunchedEffect(catalogType) {
        viewModel.setCatalogTypeAndFetch(contentType)
    }

    CatalogScreen(
        catalogType = contentType,
        navController = navController,
        state = state,
        effects = effects,
        onIntent = viewModel::handleIntent
    )
}

@Composable
fun CatalogScreen(
    catalogType: CatalogType,
    navController: AppNavigationController,
    state: CatalogState,
    effects: Flow<CatalogEffect>,
    onIntent: (CatalogIntent) -> Unit
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    CollectEffect(effects) { effect ->
        when (effect) {
            CatalogEffect.NavigateBack -> navController.popBackStack()

            is CatalogEffect.NavigateToTourIntro -> {
                navController.navigate(Screens.TourIntroScreen.createRoute(effect.id))
            }

            is CatalogEffect.NavigateToPlaceIntro -> {
                navController.navigate(Screens.PlaceIntroScreen.createRoute(effect.id))
            }

            CatalogEffect.ShowAlert -> {
                isAlertVisible = true
            }
        }
    }

    if (isAlertVisible) {
        ShowAlertDialog(
            onActionButtonClick = {
                isAlertVisible = false
                onIntent(CatalogIntent.OnBackClick)
            }
        )
    }

    val title = when (catalogType) {
        CatalogType.TOURS -> stringResource(id = R.string.screen_tour_title)
        CatalogType.PLACES -> stringResource(id = R.string.screen_place_title)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = com.blackcube.common.R.color.main_background))
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton {
                onIntent(CatalogIntent.OnBackClick)
            }

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
        }

        ShowProgressIndicator(state.isLoading)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(state.items, key = { it.id }) { item ->
                when (item) {
                    is CatalogItem.TourItem -> {
                        FullWidthCard(
                            onClick = { onIntent(CatalogIntent.OnTourItemClick(item.id)) },
                            imageUrl = item.imageUrl,
                            title = item.title,
                            description = item.description,
                            duration = item.duration,
                            isAR = item.isAR
                        )
                    }
                    is CatalogItem.PlaceItem -> {
                        FullWidthCard(
                            onClick = { onIntent(CatalogIntent.OnPlaceItemClick(item.id)) },
                            imageUrl = item.imageUrl,
                            title = item.title,
                            description = item.description
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun BackButton(
    onBackClick: () -> Unit
) {
    IconButton(
        onClick = { onBackClick.invoke() },
        modifier = Modifier
            .clip(CircleShape)
            .padding(start = 12.dp)
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
fun FullWidthCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageUrl: String,
    title: String,
    description: String,
    duration: String? = null,
    isAR: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick.invoke() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Изображение
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

            AsyncImage(
                model = imageRequest,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop,
            )
            // Контент карточки
            Column(
                modifier = Modifier
                    .padding(top = 10.dp, start = 14.dp, end = 14.dp, bottom = 14.dp)
                    .fillMaxHeight()
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorResource(id = com.blackcube.common.R.color.title_color)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = colorResource(id = com.blackcube.common.R.color.description_color),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                if (duration != null) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = "Time",
                            tint = colorResource(id = com.blackcube.common.R.color.description_color),
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .size(14.dp)
                        )
                        Text(
                            text = duration,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = colorResource(id = com.blackcube.common.R.color.description_color)
                        )
                        if (isAR) {
                            Text(
                                text = stringResource(id = R.string.AR),
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = colorResource(id = com.blackcube.common.R.color.purple),
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
