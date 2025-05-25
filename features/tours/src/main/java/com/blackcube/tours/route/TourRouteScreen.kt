package com.blackcube.tours.route

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackcube.common.ui.AlertData
import com.blackcube.common.ui.OptionsModel
import com.blackcube.common.ui.PermissionRationaleModal
import com.blackcube.common.ui.SheetOptionsSelected
import com.blackcube.common.ui.ShowAlertDialog
import com.blackcube.common.ui.ShowProgressIndicator
import com.blackcube.common.ui.openAppSettings
import com.blackcube.common.utils.CollectEffect
import com.blackcube.common.utils.map.MapUtil.navigateToMap
import com.blackcube.core.extension.checkPermission
import com.blackcube.core.navigation.AppNavigationController
import com.blackcube.core.navigation.Screens
import com.blackcube.tours.R
import com.blackcube.tours.ar.ArViewModel.Companion.ARGUMENT_COORDINATES
import com.blackcube.tours.common.components.SheetContentHistory
import com.blackcube.tours.common.components.YandexMapScreen
import com.blackcube.tours.common.models.HistoryRouteModel
import com.blackcube.tours.route.TourRouteViewModel.Companion.ARGUMENT_SELECTED_AR_COORDINATE
import com.blackcube.tours.route.components.MapArButton
import com.blackcube.tours.route.components.MapControlButton
import com.blackcube.tours.route.components.SheetContentHistoriesRoute
import com.blackcube.tours.route.store.TourRouteEffect
import com.blackcube.tours.route.store.TourRouteIntent
import com.blackcube.tours.route.store.TourRouteState
import kotlinx.coroutines.flow.Flow
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem

@Composable
fun TourRouteScreenRoot(
    tourId: String,
    navController: AppNavigationController,
    viewModel: TourRouteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect
    val context = LocalContext.current

    val savedStateFlow = navController.observeArgsAsState<String?>(
        key = ARGUMENT_SELECTED_AR_COORDINATE,
        initial = null
    )
    val arId by savedStateFlow.collectAsState()

    val textArFound = stringResource(R.string.ar_object_found)
    LaunchedEffect(arId) {
        arId?.let {
            viewModel.handleIntent(
                TourRouteIntent.OnArObjectFound(it)
            )
            Toast.makeText(context, textArFound, Toast.LENGTH_SHORT).show()
            navController.removeSavedArgs<String>(ARGUMENT_SELECTED_AR_COORDINATE)
        }
    }

    LaunchedEffect(tourId) {
        viewModel.fetchHistories(tourId)
    }

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
    navController: AppNavigationController,
    state: TourRouteState,
    effects: Flow<TourRouteEffect>,
    onIntent: (TourRouteIntent) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    var showLocationPermissionUI by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isOptionsSheetOpen by rememberSaveable { mutableStateOf(false) }
    var isHistorySheetOpen by rememberSaveable { mutableStateOf(false) }
    var isAlertVisible by remember { mutableStateOf(false) }
    var alertData by remember { mutableStateOf(AlertData()) }

    var isShowConfetti by rememberSaveable { mutableStateOf(false) }
    var konfettiPartyList by remember { mutableStateOf<List<Party>>(emptyList()) }

    var tempSaveHistoryId by remember { mutableStateOf("") }

    CollectEffect(effects) { effect ->
        when (effect) {
            TourRouteEffect.NavigateToBack -> navController.popBackStack()

            is TourRouteEffect.ShowAlert -> {
                alertData = effect.alertData
                isAlertVisible = true
            }

            is TourRouteEffect.ShowMap -> navigateToMap(
                request = effect.request,
                context = context
            )

            is TourRouteEffect.ShowConfetti -> {
                isShowConfetti = true
                konfettiPartyList = effect.party
            }

            is TourRouteEffect.SwitchArMode -> {
                navController.navigate(
                    route = Screens.ArScreen.route,
                    argument = Pair(ARGUMENT_COORDINATES, effect.coordinates)
                )
            }
        }
    }

    if (isAlertVisible) {
        ShowAlertDialog(
            alertData = alertData,
            onCancelButtonClick = {
                isAlertVisible = false
                alertData = AlertData()
            },
            onActionButtonClick = {
                isAlertVisible = false
                alertData.action?.invoke()
                alertData = AlertData()
            }
        )
    }

    if (isHistorySheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = colorResource(id = com.blackcube.common.R.color.white),
            windowInsets = WindowInsets(0.dp),
            onDismissRequest = { isHistorySheetOpen = !isHistorySheetOpen }
        ) {
            val selectedHistory = state.selectedHistory
            if (selectedHistory != null) {
                SheetContentHistory(
                    historyModel = selectedHistory,
                    onClickShowMap = { onIntent(TourRouteIntent.OnShowMapClick) }
                )
            } else {
                isHistorySheetOpen = !isHistorySheetOpen
                onIntent(TourRouteIntent.ShowAlert)
            }
        }
    }

    if (isOptionsSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = colorResource(id = com.blackcube.common.R.color.white),
            windowInsets = WindowInsets(0.dp),
            onDismissRequest = { isOptionsSheetOpen = !isOptionsSheetOpen }
        ) {
            val options = listOf(
                OptionsModel(
                    id = "showPlace",
                    value = tempSaveHistoryId,
                    title = "Подробнее о месте"
                ),
                OptionsModel(
                    id = "completePlace",
                    value = tempSaveHistoryId,
                    title = "Отметить как посещенное"
                )
            )
            SheetOptionsSelected(options) { selectedOption ->
                isOptionsSheetOpen = !isOptionsSheetOpen
                when (selectedOption.id) {
                    "showPlace" -> {
                        onIntent(TourRouteIntent.OnHistoryItemClick(selectedOption.value))
                        isHistorySheetOpen = !isHistorySheetOpen
                    }
                    "completePlace" -> {
                        onIntent(TourRouteIntent.OnHistoryCompleteClick(selectedOption.value))
                    }
                }
            }
        }
    }

    if (state.isLoading) {
        ShowProgressIndicator(state.isLoading)
        return
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
                    historyRouteModel = HistoryRouteModel(
                        id = "",
                        progress = state.routeProgress,
                        isTourStartedBefore = state.tourModel?.isStarted ?: false,
                        histories = state.tourModel?.histories ?: emptyList()
                    ),
                    isTourStarted = state.isTourStarted,
                    arObjectCount = state.arFounded,
                    onHistoryItemClick = {
                        activity?.let { activity ->
                            checkPermission(
                                activity = activity,
                                permission = Manifest.permission.ACCESS_FINE_LOCATION,
                                showInContextUI = { showLocationPermissionUI = true },
                                onGranted = {
                                    onIntent(TourRouteIntent.OnMoveLocationClick(it.lat, it.lon))
                                }
                            )
                        }
                    },
                    onStartTourClick = { onIntent(TourRouteIntent.StartTour) },
                    onOptionsClick = {
                        tempSaveHistoryId = it.id
                        isOptionsSheetOpen = true
                    }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            YandexMapScreen(
                points = state.mapPoints,
                isDarkMode = isSystemInDarkTheme(),
                buildRoute = state.isTourStarted,
                moveToLocation = state.currentLocation
            ) {
                isHistorySheetOpen = !isHistorySheetOpen
                onIntent(TourRouteIntent.OnHistoryItemClick(it.id))
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 42.dp, start = 12.dp, end = 12.dp)
            ) {
                if (state.isTourStarted) {
                    MapControlButton(
                        modifier = Modifier.align(Alignment.TopStart),
                        icon = Icons.AutoMirrored.Filled.ArrowBack
                    ) {
                        onIntent(TourRouteIntent.StopTour)
                    }
                } else {
                    MapControlButton(
                        modifier = Modifier.align(Alignment.TopStart),
                        icon = Icons.Filled.Close
                    ) {
                        onIntent(TourRouteIntent.OnBackClick)
                    }
                }
                MapControlButton(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(bottom = 160.dp),
                    icon = Icons.Filled.LocationOn,
                ) {
                    activity?.let { activity ->
                        checkPermission(
                            activity = activity,
                            permission = Manifest.permission.ACCESS_FINE_LOCATION,
                            showInContextUI = { showLocationPermissionUI = true },
                            onGranted = {
                                onIntent(TourRouteIntent.OnMoveLocationClick())
                            }
                        )
                    }
                }
                if (state.tourModel?.isAR == true) {
                    MapArButton(modifier = Modifier.align(Alignment.TopCenter)) {
                        onIntent(TourRouteIntent.OnArClick)
                    }
                }
            }
        }
    }

    PermissionRationaleModal(
        isVisible = showLocationPermissionUI,
        title = stringResource(R.string.permission_location_title),
        message = stringResource(R.string.permission_location_message),
        onPositiveClick = {
            openAppSettings(context)
            showLocationPermissionUI = false
        },
        onNegativeClick = {
            showLocationPermissionUI = false
        },
        onDismiss = {
            showLocationPermissionUI = false
        }
    )

    if (isShowConfetti) {
        KonfettiView(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f),
            parties = konfettiPartyList,
            updateListener = object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int, ) {
                    if (activeSystems == 0) isShowConfetti = false
                }
            }
        )
    }


}