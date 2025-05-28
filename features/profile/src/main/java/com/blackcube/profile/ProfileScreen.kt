package com.blackcube.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackcube.common.ui.CustomActionButtonWithIcon
import com.blackcube.common.ui.GradientLinearProgressIndicator
import com.blackcube.common.ui.ShowProgressIndicator
import com.blackcube.common.utils.CollectEffect
import com.blackcube.core.navigation.AppNavigationController
import com.blackcube.models.profile.StatsModel
import com.blackcube.profile.store.ProfileEffect
import com.blackcube.profile.store.ProfileIntent
import com.blackcube.profile.store.ProfileState
import kotlinx.coroutines.flow.Flow
import kotlin.math.abs

@Composable
fun ProfileScreenRoot(
    navController: AppNavigationController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect

    ProfileScreen(
        navController = navController,
        state = state,
        effects = effects,
        onIntent = viewModel::handleIntent
    )
}

@Composable
fun ProfileScreen(
    navController: AppNavigationController,
    state: ProfileState,
    effects: Flow<ProfileEffect>,
    onIntent: (ProfileIntent) -> Unit
) {

    CollectEffect(effects) { effect ->
        when (effect) {
            ProfileEffect.NavigateToBack -> navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.padding(top = 40.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton { onIntent(ProfileIntent.GoBack) }
                Text(
                    modifier = Modifier.padding(start = 14.dp),
                    text = stringResource(id = R.string.profile_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(com.blackcube.common.R.color.title_color)
                )
            }
            StatsContent(
                isLoading = state.isLoading,
                stats = state.stats,
            )
        }

        CustomActionButtonWithIcon(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.9f),
            backgroundColor = colorResource(com.blackcube.common.R.color.background_red),
            textColor = colorResource(com.blackcube.common.R.color.white),
            iconColor = colorResource(com.blackcube.common.R.color.white),
            text = stringResource(id = R.string.profile_exit),
            icon = Icons.AutoMirrored.Filled.Logout,
            onClick = { onIntent(ProfileIntent.OnLogout) }
        )
    }
}

@Composable
fun StatsContent(
    isLoading: Boolean,
    stats: StatsModel?
) {

    ShowProgressIndicator(isLoading)

    if (stats == null) {
        Text(
            text = stringResource(id = R.string.profile_stats_empty),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = colorResource(com.blackcube.common.R.color.description_color)
        )
        return
    }

    Text(
        modifier = Modifier.padding(vertical = 14.dp),
        text = stringResource(id = R.string.profile_stats_title),
        fontSize = 21.sp,
        fontWeight = FontWeight.Bold,
        color = colorResource(com.blackcube.common.R.color.title_color)
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            StatsCard(
                title = stringResource(id = R.string.profile_stats_tours),
                total = stats.totalTours,
                userValue = stats.userFinishedTours,
                pctAboveAvg = stats.toursPctAbove,
                rate = stats.userFinishedTours / stats.totalTours.toFloat()
            )
        }
        item {
            StatsCard(
                title = stringResource(id = R.string.profile_stats_histories),
                total = stats.totalHistories,
                userValue = stats.userDoneHistories,
                pctAboveAvg = stats.histPctAbove,
                rate = stats.userDoneHistories / stats.totalHistories.toFloat()
            )
        }
        item {
            StatsCard(
                title = stringResource(id = R.string.profile_stats_ar),
                total = stats.totalArObjects,
                userValue = stats.userScannedAr,
                pctAboveAvg = stats.arPctAbove,
                rate = stats.userScannedAr / stats.totalArObjects.toFloat()
            )
        }
        item {
            Spacer(modifier = Modifier.height(78.dp))
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    total: Long,
    userValue: Long,
    pctAboveAvg: Double,
    rate: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(com.blackcube.common.R.color.title_color)
            )
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Всего в системе: $total",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = colorResource(com.blackcube.common.R.color.description_color)
            )
            Spacer(Modifier.height(4.dp))

            Text(
                text = "Ваш результат: $userValue",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = colorResource(com.blackcube.common.R.color.description_color)
            )

            if (pctAboveAvg > 0.0) {
                Spacer(Modifier.height(4.dp))

                val pctText = "Вы лучше других ${"%.2f".format(abs(pctAboveAvg))}% пользователей"
                Text(
                    text = pctText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(com.blackcube.common.R.color.tour_status_green_text)
                )
            }
            Spacer(Modifier.height(12.dp))

            GradientLinearProgressIndicator(rate)
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