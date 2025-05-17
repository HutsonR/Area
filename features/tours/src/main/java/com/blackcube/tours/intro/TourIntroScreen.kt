package com.blackcube.tours.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.blackcube.common.ui.CustomActionButton
import com.blackcube.common.ui.GradientLinearProgressIndicator
import com.blackcube.common.ui.SectionTitle
import com.blackcube.common.ui.ShowAlertDialog
import com.blackcube.common.ui.ShowProgressIndicator
import com.blackcube.common.utils.map.MapUtil.navigateToMap
import com.blackcube.core.navigation.AppNavigationController
import com.blackcube.core.navigation.Screens
import com.blackcube.core.util.CollectEffect
import com.blackcube.tours.R
import com.blackcube.tours.common.components.SheetContentHistory
import com.blackcube.tours.intro.store.models.TourIntroEffect
import com.blackcube.tours.intro.store.models.TourIntroIntent
import com.blackcube.tours.intro.store.models.TourIntroState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

@Composable
fun TourIntroScreenRoot(
    tourId: String,
    navController: AppNavigationController,
    viewModel: TourIntroViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect

    LaunchedEffect(tourId) {
        viewModel.fetchTour(tourId)
    }

    TourIntroScreen(
        navController = navController,
        state = state,
        effects = effects,
        onIntent = viewModel::handleIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourIntroScreen(
    navController: AppNavigationController,
    state: TourIntroState,
    effects: Flow<TourIntroEffect>,
    onIntent: (TourIntroIntent) -> Unit
) {
    val context = LocalContext.current
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isHistorySheetOpen by rememberSaveable { mutableStateOf(false) }
    var isAlertVisible by remember { mutableStateOf(false) }

    CollectEffect(effects) { effect ->
        when (effect) {
            TourIntroEffect.NavigateToBack -> navController.popBackStack()
            
            is TourIntroEffect.NavigateToStartTour -> {
                navController.navigate(Screens.TourRouteScreen.createRoute(effect.id))
            }

            is TourIntroEffect.ShowAlert -> {
                isAlertVisible = true
            }

            is TourIntroEffect.ShowMap -> navigateToMap(
                request = effect.request,
                context = context
            )
        }
    }

    if (isAlertVisible) {
        ShowAlertDialog(
            onActionButtonClick = {
                isAlertVisible = false
                onIntent(TourIntroIntent.OnBackClick)
            }
        )
    }

    if (isHistorySheetOpen) {
        ModalBottomSheet(
            sheetState = historySheetState,
            containerColor = colorResource(id = com.blackcube.common.R.color.white),
            windowInsets = WindowInsets(0.dp),
            onDismissRequest = { isHistorySheetOpen = !isHistorySheetOpen }
        ) {
            val selectedHistory = state.selectedHistory
            if (selectedHistory != null) {
                SheetContentHistory(
                    historyModel = selectedHistory,
                    onClickShowMap = { onIntent(TourIntroIntent.OnShowMapClick) }
                )
            } else {
                onIntent(TourIntroIntent.ShowAlert)
                isHistorySheetOpen = !isHistorySheetOpen
            }
        }
    }

    if (state.isLoading) {
        BackButton(onBackClick = { onIntent(TourIntroIntent.OnBackClick) })
        ShowProgressIndicator(state.isLoading)
    }

    val tourModel = state.tourModel ?: return // Показ алерта при null в VM

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(com.blackcube.common.R.color.main_background))
    ) {
        LazyColumn {
            item {
                Header(
                    imageUrl = tourModel.imageUrl,
                    title = tourModel.title,
                    description = tourModel.description,
                    duration = tourModel.duration,
                    distance = tourModel.distance,
                    isCompleted = tourModel.isCompleted
                )

                ArAvailableBlock(isArAvailable = tourModel.isAR)
            }
            item {
                SectionTitle(
                    stringResource(id = R.string.history_title),
                    Modifier.padding(
                        top = if (tourModel.isAR) 20.dp else 0.dp,
                        start = 20.dp,
                        end = 24.dp,
                        bottom = 14.dp
                    )
                )
            }
            itemsIndexed(state.tourModel.histories, key = { _, item -> item.id }) { _, item ->
                HistoryItem(
                    onClick = {
                        isHistorySheetOpen = !isHistorySheetOpen
                        onIntent(TourIntroIntent.OnHistoryItemClick(item))
                    },
                    number = item.ordinalNumber,
                    title = item.title,
                    description = item.description,
                    isCompleted = item.isCompleted
                )
            }
            item {
                val tourIsStarted = state.tourModel.isStarted
                val tourIsCompleted = state.tourModel.isCompleted
                var buttonTitle = stringResource(id = R.string.history_go_button)
                var buttonTopPadding = 30.dp

                when {
                    tourIsStarted && tourIsCompleted -> {
                        buttonTopPadding = 12.dp
                    }

                    tourIsStarted -> {
                        buttonTitle = stringResource(id = R.string.history_continue_button)
                        buttonTopPadding = 12.dp
                    }
                }

                Box(modifier = Modifier.padding(start = 20.dp, top = 30.dp, end = 20.dp)) {
                    GradientLinearProgressIndicator(state.tourProgress)
                }

                CustomActionButton(
                    onClick = { onIntent(TourIntroIntent.OnStartTourClick) },
                    modifier = Modifier.padding(start = 20.dp, top = buttonTopPadding, end = 20.dp, bottom = 30.dp),
                    backgroundColor = colorResource(com.blackcube.common.R.color.purple),
                    textColor = Color.White,
                    text = buttonTitle
                )
            }
        }

        BackButton(onBackClick = { onIntent(TourIntroIntent.OnBackClick) })
    }
}

@Preview
@Composable
fun PreviewTourScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(com.blackcube.common.R.color.main_background))
            .padding(vertical = 24.dp)
    ) {
        Header(
            imageUrl = "https://i.pinimg.com/originals/29/36/06/2936068fffd819ba4c2abeaf7dd04206.png",
            title = "Легенды подземелий",
            description = "Погрузитесь в мрачные подземелья и разгадайте тайны прошлого",
            duration = "1.5 часа",
            distance = "12 км.",
            isCompleted = true
        )

        ArAvailableBlock(isArAvailable = true)

        SectionTitle(
            stringResource(id = R.string.history_title),
            Modifier.padding(
                top = 20.dp,
                start = 20.dp,
                end = 24.dp,
                bottom = 14.dp
            )
        )

        CustomActionButton(
            onClick = { },
            modifier = Modifier.padding(20.dp),
            backgroundColor = colorResource(com.blackcube.common.R.color.purple),
            textColor = Color.White,
            text = stringResource(id = R.string.history_go_button)
        )
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
fun Header(
    imageUrl: String,
    title: String,
    description: String,
    duration: String,
    distance: String,
    isCompleted: Boolean
) {
    Column {
        // Image with Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
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
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop,
            )
        }

        Column(
            modifier = Modifier
                .offset(y = (-40).dp)
                .fillMaxWidth(fraction = .9f)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(
                    top = 20.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 14.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 21.sp,
                textAlign = TextAlign.Center,
                color = colorResource(id = com.blackcube.common.R.color.title_color)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = colorResource(id = com.blackcube.common.R.color.description_color)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        color = colorResource(
                            id =
                            if (isCompleted) {
                                com.blackcube.common.R.color.tour_status_green_background
                            } else {
                                com.blackcube.common.R.color.tour_status_gray_background
                            }
                        )
                    )
                    .padding(
                        vertical = 6.dp,
                        horizontal = 14.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isCompleted) {
                        Icons.Outlined.Check
                    } else {
                        Icons.Outlined.Close
                    },
                    contentDescription = "Status",
                    modifier = Modifier.size(14.dp),
                    tint = colorResource(
                        id =
                        if (isCompleted) {
                            com.blackcube.common.R.color.tour_status_green_text
                        } else {
                            com.blackcube.common.R.color.tour_status_gray_text
                        }
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isCompleted) {
                        stringResource(id = R.string.status_completed)
                    } else {
                        stringResource(id = R.string.status_not_completed)
                    },
                    fontSize = 14.sp,
                    color = colorResource(
                        id = if (isCompleted) {
                            com.blackcube.common.R.color.tour_status_green_text
                        } else {
                            com.blackcube.common.R.color.tour_status_gray_text
                        }
                    )
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-26).dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Timer,
                contentDescription = "Time",
                modifier = Modifier.size(18.dp),
                tint = colorResource(id = com.blackcube.common.R.color.description_color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = duration,
                fontSize = 14.sp,
                color = colorResource(id = com.blackcube.common.R.color.description_color)
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Outlined.DirectionsWalk,
                contentDescription = "Distance",
                modifier = Modifier.size(18.dp),
                tint = colorResource(id = com.blackcube.common.R.color.description_color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = distance,
                fontSize = 14.sp,
                color = colorResource(id = com.blackcube.common.R.color.description_color)
            )
        }
    }
}

@Composable
fun ArAvailableBlock(
    isArAvailable: Boolean
) {
    if (!isArAvailable) return
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
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
                horizontal = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ar_icon),
            contentDescription = "AR",
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = R.string.ar_available),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun HistoryItem(
    onClick: () -> Unit,
    number: Int,
    title: String,
    description: String,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 3.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick.invoke() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "${number}.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(com.blackcube.common.R.color.title_color),
            )
            Column(
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(com.blackcube.common.R.color.title_color),
                )
                Text(
                    modifier = Modifier.padding(top = 6.dp),
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(com.blackcube.common.R.color.description_color),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (isCompleted) {
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = "Status",
                            modifier = Modifier.size(14.dp),
                            tint = colorResource(id = com.blackcube.common.R.color.tour_status_green_text)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.history_status_completed),
                            fontSize = 12.sp,
                            color = colorResource(id = com.blackcube.common.R.color.tour_status_green_text)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HistoryItemPreview() {
    HistoryItem(
        onClick = {},
        number = 1,
        title = "Какой-то заголовок истории",
        description = "Описание истории очень очень оооочень длинное, нужно просто создать эффект многоточья",
        isCompleted = true
    )
}