package com.blackcube.tours.route

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.blackcube.common.ui.ShowAlertDialog
import com.blackcube.common.utils.CollectEffect
import com.blackcube.tours.common.components.SheetContentHistoriesRoute
import com.blackcube.tours.common.components.SheetContentHistory
import com.blackcube.tours.common.components.YandexMapScreen
import com.blackcube.tours.common.models.HistoryRouteModel
import com.blackcube.tours.common.utils.MapUtil.navigateToMap
import com.blackcube.tours.route.components.MapArButton
import com.blackcube.tours.route.components.MapControlButton
import com.blackcube.tours.route.store.TourRouteEffect
import com.blackcube.tours.route.store.TourRouteIntent
import com.blackcube.tours.route.store.TourRouteState
import kotlinx.coroutines.flow.Flow

@Composable
fun TourRouteScreenRoot(
    tourId: String,
    navController: NavController,
    viewModel: TourRouteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect

    TourRouteScreen(
        navController = navController,
        state = state,
        effects = effects,
        onIntent = viewModel::handleIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourRouteScreen(
    navController: NavController,
    state: TourRouteState,
    effects: Flow<TourRouteEffect>,
    onIntent: (TourRouteIntent) -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isHistorySheetOpen by rememberSaveable { mutableStateOf(false) }
    var isAlertVisible by remember { mutableStateOf(false) }

    CollectEffect(effects) { effect ->
        when (effect) {
            TourRouteEffect.NavigateToBack -> navController.popBackStack()

            is TourRouteEffect.ShowAlert -> {
                isAlertVisible = true
            }

            is TourRouteEffect.ShowMap -> navigateToMap(
                request = effect.request,
                context = context
            )

            TourRouteEffect.SwitchArMode -> Unit
        }
    }

    if (isAlertVisible) {
        ShowAlertDialog(
            onButtonClick = {
                isAlertVisible = false
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
            state.selectedHistory?.let {
                SheetContentHistory(
                    historyModel = it,
                    onClickShowMap = { onIntent(TourRouteIntent.OnShowMapClick) }
                )
            } ?: onIntent(TourRouteIntent.ShowAlert)
        }
    }

    BottomSheetScaffold(
        sheetContainerColor = colorResource(id = com.blackcube.common.R.color.white),
        sheetPeekHeight = 100.dp,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = screenHeight / 2)
            ) {
                SheetContentHistoriesRoute(
                    onHistoryItemClick = {
                        isHistorySheetOpen = !isHistorySheetOpen
                        onIntent(TourRouteIntent.OnHistoryItemClick(it.id))
                    },
                    historyRouteModel = HistoryRouteModel(
                        id = "",
                        histories = state.histories
                    )
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            YandexMapScreen(
                points = state.mapPoints,
                isDarkMode = isSystemInDarkTheme(),
                currentLocation = state.currentLocation
            ) {
                isHistorySheetOpen = !isHistorySheetOpen
                onIntent(TourRouteIntent.OnHistoryItemClick(it.id))
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 42.dp, start = 12.dp, end = 12.dp)
            ) {
                MapControlButton(
                    modifier = Modifier.align(Alignment.TopStart),
                    icon = Icons.AutoMirrored.Filled.ArrowBack
                ) {
                    onIntent(TourRouteIntent.OnBackClick)
                }
                MapControlButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 120.dp),
                    icon = Icons.Filled.LocationOn
                ) {
                    onIntent(TourRouteIntent.OnCurrentLocationClick)
                }
                MapArButton(modifier = Modifier.align(Alignment.TopCenter)) {
                    onIntent(TourRouteIntent.OnArClick)
                }
            }
        }
    }

}