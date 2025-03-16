package com.blackcube.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.blackcube.common.ui.CustomActionButton
import com.blackcube.common.ui.CustomActionButtonWithIcon
import com.blackcube.common.ui.SectionTitle
import com.blackcube.common.utils.CollectEffect
import com.blackcube.core.navigation.Screens
import com.blackcube.home.store.models.HomeEffect
import com.blackcube.home.store.models.HomeIntent
import com.blackcube.home.store.models.HomeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

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
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    CollectEffect(effects) { effect ->
        when (effect) {
            is HomeEffect.NavigateToExcursion -> {
                navController.navigate(Screens.TourIntroScreen.createRoute(effect.id))
            }
        }
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
            )

            Spacer(modifier = Modifier.height(20.dp))

            MyAdventuresSection()

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
        itemsIndexed(state.lists, key = { _, item -> item.id }) { _, item ->
            ExcursionCard(
                onClick = { onIntent(HomeIntent.OnExcursionClick(item.id)) },
                imageUrl = item.imageUrl,
                title = item.title,
                description = item.description,
                duration = item.duration,
                isAR = item.isAR
            )
        }
        // todo добавить потом ещё места и события. Горизонтальный скролл, как RussPass
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
        )

        Spacer(modifier = Modifier.height(20.dp))

        MyAdventuresSection()

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
    progress: Float
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

                GradientLinearProgressIndicator(progress)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${(progress * 100).roundToInt()}%",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "100%",
                        fontSize = 12.sp
                    )
                }

            }

            CustomActionButtonWithIcon(
                onClick = {},
                backgroundColor = colorResource(com.blackcube.common.R.color.purple),
                textColor = Color.White,
                iconColor = Color.White,
                text = stringResource(id = R.string.button_continue),
                icon = Icons.AutoMirrored.Filled.ArrowForward
            )
        }
    }
}

@Composable
fun GradientLinearProgressIndicator(progress: Float) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            colorResource(com.blackcube.common.R.color.light_purple),
            colorResource(com.blackcube.common.R.color.dark_purple)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp)
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress)
                .fillMaxHeight()
                .background(brush = gradientBrush)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentQuestCardPreview() {
    CurrentQuestCard(
        text = "Тайны старого города",
        progress = 0.4f
    )
}

@Composable
fun MyAdventuresSection() {
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
                    onClick = {},
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
    MyAdventuresSection()
}

@Composable
fun ExcursionCard(
    onClick: () -> Unit,
    imageUrl: String,
    title: String,
    description: String,
    duration: String,
    isAR: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick.invoke() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            val context = LocalContext.current
            val placeholder = com.blackcube.common.R.drawable.placeholder

            val imageRequest = ImageRequest.Builder(context)
                .data(imageUrl)
//                .listener(listener)
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
                    .height(140.dp),
                contentScale = ContentScale.Crop,
            )
            // Content
            Column(
                modifier = Modifier.padding(
                    top = 10.dp,
                    start = 14.dp,
                    end = 14.dp,
                    bottom = 14.dp
                )
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colorResource(id = com.blackcube.common.R.color.title_color)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = colorResource(id = com.blackcube.common.R.color.description_color),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row (
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

@Preview(showBackground = true)
@Composable
fun ExcursionCardPreview() {
    ExcursionCard(
        onClick = {},
        imageUrl = "https://example.com/image.jpg",
        title = "Тайны старого города",
        description = "Походите по старым закаулкам города в поисках чего-то необычного, может загадочного",
        duration = "40 мин.",
        isAR = true
    )
}