package com.blackcube.tours.route

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.blackcube.tours.common.components.TourPoint
import com.blackcube.tours.common.models.HistoryRouteModel
import com.blackcube.tours.common.utils.MapUtil.navigateToMap
import com.blackcube.tours.route.components.MapArButton
import com.blackcube.tours.route.components.MapBackButton
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
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }
    var alertHandled by remember { mutableStateOf(false) }

    CollectEffect(effects) { effect ->
        when (effect) {
            TourRouteEffect.NavigateToBack -> navController.popBackStack()

            is TourRouteEffect.ShowAlert -> {
                if (!alertHandled) {
                    showAlert = true
                    alertHandled = true
                }
            }

            is TourRouteEffect.ShowMap -> navigateToMap(
                request = effect.request,
                context = context
            )

            TourRouteEffect.SwitchArMode -> Unit
            TourRouteEffect.ZoomMinus -> Unit
            TourRouteEffect.ZoomPlus -> Unit
        }
    }

    if (showAlert) {
        ShowAlertDialog(
            onButtonClick = {
                showAlert = false
                alertHandled = true
            }
        )
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = colorResource(id = com.blackcube.common.R.color.white),
            windowInsets = WindowInsets(0.dp),
            onDismissRequest = { isSheetOpen = !isSheetOpen }
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
            SheetContentHistoriesRoute(
                onHistoryItemClick = {
                    isSheetOpen = !isSheetOpen
                    onIntent(TourRouteIntent.OnHistoryItemClick(it))
                },
                historyRouteModel = HistoryRouteModel(
                    id = "",
                    histories = state.histories
                )
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            YandexMapScreen(
                points = listOf(
                    TourPoint(1,47.2357, 39.7015, "Название точки"),
                    TourPoint(2,47.236732, 39.706941, "Работает?")
                ),
                isDarkMode = isSystemInDarkTheme()
            ) {
                Toast.makeText(context, "Точка: ${it.title}", Toast.LENGTH_SHORT).show()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 42.dp, start = 12.dp, end = 12.dp)
            ) {
                MapBackButton(modifier = Modifier.align(Alignment.TopStart)) {
                    onIntent(TourRouteIntent.OnBackClick)
                }
                MapArButton(modifier = Modifier.align(Alignment.TopCenter)) {
                    onIntent(TourRouteIntent.OnArClick)
                }
            }
        }
    }

}