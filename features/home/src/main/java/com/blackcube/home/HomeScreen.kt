package com.blackcube.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.blackcube.catalog.models.CatalogType
import com.blackcube.common.ui.AlertData
import com.blackcube.common.ui.CustomActionButton
import com.blackcube.common.ui.CustomActionButtonWithIcon
import com.blackcube.common.ui.GradientLinearProgressIndicator
import com.blackcube.common.ui.SectionTitle
import com.blackcube.common.ui.ShowAlertDialog
import com.blackcube.common.ui.ShowProgressIndicator
import com.blackcube.common.utils.CollectEffect
import com.blackcube.core.navigation.Screens
import com.blackcube.home.HomeViewModel.Companion.MAX_PLACES_ITEMS
import com.blackcube.home.HomeViewModel.Companion.MAX_TOUR_ITEMS
import com.blackcube.home.store.models.HomeEffect
import com.blackcube.home.store.models.HomeIntent
import com.blackcube.home.store.models.HomeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

@Composable
fun HomeScreenRoot(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect

    HomeScreen(
        navController = navController,
        state = state,
        effects = effects,
        onIntent = viewModel::handleIntent
    )
}

@Composable
fun HomeScreen(
    navController: NavController,
    state: HomeState,
    effects: Flow<HomeEffect>,
    onIntent: (HomeIntent) -> Unit
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    CollectEffect(effects) { effect ->
        when (effect) {
            is HomeEffect.NavigateToTourIntro -> {
                navController.navigate(Screens.TourIntroScreen.createRoute(effect.id))
            }

            is HomeEffect.NavigateToPlaceIntro -> {
                navController.navigate(Screens.PlaceIntroScreen.createRoute(effect.id))
            }

            is HomeEffect.NavigateToAllCards -> {
                navController.navigate(Screens.AllCardsScreen.createRoute(effect.cardType))
            }

            HomeEffect.ShowAlert -> {
                isAlertVisible = true
            }
        }
    }

    if (isAlertVisible) {
        ShowAlertDialog(
            alertData = AlertData(
                actionButtonTitle = com.blackcube.common.R.string.button_update
            ),
            onActionButtonClick = {
                isAlertVisible = false
                onIntent(HomeIntent.Reload)
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(com.blackcube.common.R.color.main_background))
            .padding(
                top = 48.dp,
                bottom = 24.dp
            )
    ) {
        item {
            state.currentStartedQuest?.let { quest ->
                SectionTitle(
                    text = stringResource(id = R.string.current_quest_title),
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 14.dp
                    )
                )

                CurrentQuestCard(
                    quest.tourModel.title,
                    progress = quest.progress
                ) {
                    onIntent(HomeIntent.OnContinueTourClick)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
            MyAdventuresSection {
                onIntent(HomeIntent.OnSeeStatsClick)
            }
        }

        if (state.isLoading) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ShowProgressIndicator(state.isLoading)
            }
        }

        if (state.placesItems.isEmpty() && state.tourItems.isEmpty() && !state.isLoading) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp, horizontal = 24.dp),
                    text = stringResource(id = R.string.empty),
                    color = colorResource(com.blackcube.common.R.color.description_color),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (state.tourItems.isNotEmpty()) {
            item {
                SectionTitle(
                    text = stringResource(id = R.string.tour_title),
                    modifier = Modifier.padding(
                        top = 20.dp,
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 14.dp
                    )
                )

                LazyRow {
                    itemsIndexed(state.tourItems.take(MAX_TOUR_ITEMS), key = { _, item -> item.id }) { index, item ->
                        val paddingPair = when (index) {
                            0 -> 24.dp to 12.dp
                            state.tourItems.lastIndex -> 0.dp to 24.dp
                            else -> 0.dp to 12.dp
                        }
                        CardItem(
                            modifier = Modifier.padding(start = paddingPair.first, end = paddingPair.second),
                            imageUrl = item.imageUrl,
                            title = item.title,
                            description = item.description,
                            duration = item.duration,
                            isAR = item.isAR,
                            onClick = { onIntent(HomeIntent.OnTourItemClick(item.id)) }
                        )
                    }
                }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp)) {
                    CustomActionButtonWithIcon(
                        backgroundColor = colorResource(com.blackcube.common.R.color.purple),
                        textColor = colorResource(com.blackcube.common.R.color.white),
                        iconColor = colorResource(com.blackcube.common.R.color.white),
                        text = stringResource(id = R.string.tour_button),
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = { onIntent(HomeIntent.OnSeeAllCardsClick(CatalogType.TOURS)) }
                    )
                }
            }
        }

        if (state.placesItems.isNotEmpty()) {
            item {
                SectionTitle(
                    text = stringResource(id = R.string.places_title),
                    modifier = Modifier.padding(
                        top = 36.dp,
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 14.dp
                    )
                )

                LazyRow {
                    itemsIndexed(state.placesItems.take(MAX_PLACES_ITEMS), key = { _, item -> item.id }) { index, item ->
                        val paddingPair = when (index) {
                            0 -> 24.dp to 12.dp
                            state.placesItems.lastIndex -> 0.dp to 24.dp
                            else -> 0.dp to 12.dp
                        }
                        CardItem(
                            modifier = Modifier.padding(start = paddingPair.first, end = paddingPair.second),
                            onClick = { onIntent(HomeIntent.OnPlaceItemClick(item.id)) },
                            imageUrl = item.imageUrl,
                            title = item.title,
                            description = item.description
                        )
                    }
                }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp)) {
                    CustomActionButtonWithIcon(
                        backgroundColor = colorResource(com.blackcube.common.R.color.purple),
                        textColor = colorResource(com.blackcube.common.R.color.white),
                        iconColor = colorResource(com.blackcube.common.R.color.white),
                        text = stringResource(id = R.string.places_button),
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = { onIntent(HomeIntent.OnSeeAllCardsClick(CatalogType.PLACES)) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(com.blackcube.common.R.color.main_background))
            .padding(vertical = 24.dp)
    ) {
        SectionTitle(
            stringResource(id = R.string.current_quest_title),
            Modifier.padding(
                start = 24.dp,
                end = 24.dp,
                bottom = 14.dp
            )
        )

        CurrentQuestCard(
            "Тайны старого города",
            progress = 0.4f
        ) {}

        Spacer(modifier = Modifier.height(20.dp))

        MyAdventuresSection({})

        SectionTitle(
            stringResource(id = R.string.tour_title),
            Modifier.padding(
                top = 20.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = 14.dp
            )
        )
    }
}

@Preview
@Composable
fun PreviewSectionTitle() {
    SectionTitle("Квесты", Modifier)
}

@Composable
fun CurrentQuestCard(
    text: String,
    progress: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Column(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(com.blackcube.common.R.color.title_color)
                )

                Box(
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    GradientLinearProgressIndicator(progress)
                }
            }

            CustomActionButtonWithIcon(
                backgroundColor = colorResource(com.blackcube.common.R.color.purple),
                textColor = colorResource(com.blackcube.common.R.color.white),
                iconColor = colorResource(com.blackcube.common.R.color.white),
                text = stringResource(id = R.string.button_continue),
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentQuestCardPreview() {
    CurrentQuestCard(
        text = "Тайны старого города",
        progress = 0.4f
    ) {}
}

@Composable
fun MyAdventuresSection(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            colorResource(com.blackcube.common.R.color.dark_orange),
                            colorResource(com.blackcube.common.R.color.light_orange)
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Column(modifier = Modifier.padding(4.dp)) {
                    Text(
                        text = stringResource(id = R.string.adventure_title),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = R.string.adventure_text),
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                CustomActionButton(
                    onClick = onClick,
                    backgroundColor = Color.White,
                    textColor = Color.Black,
                    text = stringResource(id = R.string.button_ofcourse)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAdventuresSectionPreview() {
    MyAdventuresSection({})
}

@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    description: String,
    duration: String? = null,
    isAR: Boolean = false,
    onClick: () -> Unit
) {
    val height = if (duration != null) 260.dp else 240.dp
    Card(
        modifier = modifier
            .width(200.dp)
            .height(height)
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
                    .height(120.dp),
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
                    fontSize = 18.sp,
                    color = colorResource(id = com.blackcube.common.R.color.title_color),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = colorResource(id = com.blackcube.common.R.color.description_color),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                if (duration != null) {
                    Row(
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

@Preview(showBackground = true)
@Composable
fun ExcursionCardPreview() {
    CardItem(
        onClick = {},
        imageUrl = "https://example.com/image.jpg",
        title = "Тайны старого города",
        description = "Походите по старым закаулкам города в поисках чего-то необычного, может загадочного",
        duration = "40 мин.",
        isAR = true
    )
}

@Preview(showBackground = true)
@Composable
fun PlaceCardPreview() {
    CardItem(
        onClick = {},
        imageUrl = "https://example.com/image.jpg",
        title = "Тайны старого города",
        description = "Походите по старым закаулкам города в поисках чего-то необычного, может загадочного"
    )
}