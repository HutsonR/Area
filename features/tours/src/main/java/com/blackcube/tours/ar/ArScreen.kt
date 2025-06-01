package com.blackcube.tours.ar

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackcube.common.ui.CustomActionButton
import com.blackcube.common.ui.CustomTextInput
import com.blackcube.common.utils.CollectEffect
import com.blackcube.core.extension.checkPermission
import com.blackcube.core.navigation.AppNavigationController
import com.blackcube.tours.R
import com.blackcube.tours.ar.ArViewModel.Companion.ARGUMENT_COORDINATES
import com.blackcube.tours.ar.store.ArEffect
import com.blackcube.tours.ar.store.ArIntent
import com.blackcube.tours.ar.store.ArState
import com.blackcube.tours.ar.store.models.ArModel
import com.blackcube.tours.ar.store.models.ArType
import com.blackcube.tours.route.TourRouteViewModel.Companion.ARGUMENT_SELECTED_AR_COORDINATE
import com.google.android.filament.Engine
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Earth
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.ViewNode2
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import io.github.sceneview.rememberViewNodeManager
import kotlinx.coroutines.flow.Flow

@Composable
fun ArScreenRoot(
    navController: AppNavigationController,
    viewModel: ArViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect

    val savedStateFlow = navController.observeArgsAsState<arArgs?>(
        key = ARGUMENT_COORDINATES,
        initial = null
    )
    val coords by savedStateFlow.collectAsState()

    LaunchedEffect(coords) {
        coords?.let {
            viewModel.setDataFromArgument(it)
            navController.removeSavedArgs<List<ArModel>>(ARGUMENT_COORDINATES)
        }
    }

    ArScreen(
        navController = navController,
        state = state,
        effects = effects,
        onIntent = viewModel::handleIntent
    )
}

@SuppressLint("MissingPermission")
@Composable
fun ArScreen(
    navController: AppNavigationController,
    state: ArState,
    effects: Flow<ArEffect>,
    onIntent: (ArIntent) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var lastPosition by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    var showLocationPermissionUI by remember { mutableStateOf(false) }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                fetchLastLocation(fusedLocationClient) { lat, lon ->
                    onIntent(ArIntent.UpdateGpsLocation(lat, lon))
                    lastPosition = Pair(lat, lon)
                }
            } else {
                Log.d("ArScreen", "Location permission denied")
            }
        }

    LaunchedEffect(Unit) {
        activity?.let { act ->
            checkPermission(
                activity = act,
                permission = Manifest.permission.ACCESS_FINE_LOCATION,
                showInContextUI = {
                    showLocationPermissionUI = true
                },
                onGranted = {
                    fetchLastLocation(fusedLocationClient) { lat, lon ->
                        onIntent(ArIntent.UpdateGpsLocation(lat, lon))
                        lastPosition = Pair(lat, lon)
                    }
                }
            )
        }
    }

    if (showLocationPermissionUI) {
        AlertDialog(
            onDismissRequest = { showLocationPermissionUI = false },
            title = { Text("Требуется разрешение") },
            text = { Text("Для получения координат нужны разрешения на геолокацию.") },
            confirmButton = {
                TextButton(onClick = {
                    showLocationPermissionUI = false
                    activity?.let { act ->
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }) {
                    Text("Разрешить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationPermissionUI = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    CollectEffect(effects) { effect ->
        when (effect) {
            ArEffect.NavigateToBack -> navController.popBackStack()
            is ArEffect.NavigateWithId -> {
                navController.popBackStack(
                    argument = Pair(ARGUMENT_SELECTED_AR_COORDINATE, effect.id)
                )
            }
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var pendingHitResult by remember { mutableStateOf<HitResult?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val materialLoader = rememberMaterialLoader(engine)
        val cameraNode = rememberARCameraNode(engine)
        val childNodes = rememberNodes()
        val view = rememberView(engine)
        val windowManager = rememberViewNodeManager()

        val collisionSystem = rememberCollisionSystem(view)

        var earthInstance by remember { mutableStateOf<Earth?>(null) }
        var frame by remember { mutableStateOf<Frame?>(null) }

        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            collisionSystem = collisionSystem,
            cameraNode = cameraNode,
            planeRenderer = false,
            cameraStream = rememberARCameraStream(materialLoader),
            sessionConfiguration = { session, config ->
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    true -> Config.DepthMode.AUTOMATIC
                    else -> Config.DepthMode.DISABLED
                }
                if (session.isGeospatialModeSupported(Config.GeospatialMode.ENABLED)) {
                    config.geospatialMode = Config.GeospatialMode.ENABLED
                }
            },
            onSessionCreated = { session ->
                earthInstance = session.earth
            },
            onSessionUpdated = { session, updatedFrame ->
                frame = updatedFrame

                val earth = earthInstance
                if (
                    earth != null
                    && earth.trackingState == TrackingState.TRACKING
                    && updatedFrame.camera.trackingState == TrackingState.TRACKING
                    && childNodes.isEmpty()
                    )
                {
                    val geoPose = earth.cameraGeospatialPose
                    onIntent(ArIntent.UpdateArLocation(geoPose.latitude, geoPose.longitude))

                    val nearestModels = state.selectedObjectModels
                    val nearestObjectModel = state.selectedObjectModels.firstOrNull { it.type == ArType.OBJECT }
                    if (nearestModels.isNotEmpty() && nearestObjectModel != null) {
                        Log.d("debugTag", "IN LOCATION")
                        val plane = updatedFrame
                            .getUpdatedPlanes()
                            .firstOrNull { it.trackingState == TrackingState.TRACKING
                                    && it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }

                        plane?.let {
                            Log.d("debugTag", "PLANE FOUND")
                            val anchor = it.createAnchor(it.centerPose)

                            childNodes += createAnchorObjectNode(
                                engine = engine,
                                modelLoader = modelLoader,
                                anchor = anchor,
                                objectModel = nearestObjectModel,
                                arModelPaths = state.arModelPaths
                            )
                            val model = nearestModels.firstOrNull { it.type == ArType.TEXT }
                            val textAnchorNode = createTextAnchorNode(
                                context = context,
                                engine = engine,
                                anchor = anchor,
                                objectModel = model!!,
                                windowManager = windowManager,
                                materialLoader = materialLoader
                            )
                            childNodes += textAnchorNode
                        }
                    }

                }
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { motionEvent, node ->
                    if (node != null) {
                        val anchorNode = when (node) {
                            is AnchorNode -> node
                            else -> node.parent as? AnchorNode
                        }

                        anchorNode?.name?.let {
                            onIntent(ArIntent.OnObjectNodeClick(it))
                        }
                    } else {
                        val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                        hitResults?.firstOrNull { hit ->
                            val trackable = hit.trackable
                            (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose))
                        }?.let { hitResult ->
                            hitResult.createAnchorOrNull()?.let {
                                pendingHitResult = hitResult
                            }
                        }
                    }
                })
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(vertical = 86.dp, horizontal = 24.dp)
                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 16.sp,
                text = if (childNodes.isEmpty()) {
                    stringResource(R.string.scan_with_phone)
                } else {
                    stringResource(R.string.tap_to_scan_model)
                }
            )
        }

        BackButton {
            onIntent(ArIntent.OnBackClick)
        }

        CustomActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp),
            text = stringResource(id = R.string.ar_add_comment),
            backgroundColor = colorResource(com.blackcube.common.R.color.button_background_gray),
            textColor = colorResource(com.blackcube.common.R.color.title_color),
            onClick = {
                activity?.let { act ->
                    checkPermission(
                        activity = act,
                        permission = Manifest.permission.ACCESS_FINE_LOCATION,
                        showInContextUI = {
                            showLocationPermissionUI = true
                        },
                        onGranted = {
                            fetchLastLocation(fusedLocationClient) { lat, lon ->
                                onIntent(ArIntent.UpdateGpsLocation(lat, lon))
                                lastPosition = Pair(lat, lon)
                                showDialog = true
                            }
                        }
                    )
                }
            }
        )

        if (showDialog) {
            CommentInputDialog(
                onConfirm = { text ->
                    showDialog = false
                    pendingHitResult?.let { hitResult ->
                        val anchor = hitResult.createAnchorOrNull()
                        if (anchor != null) {
                            // Собираем ArModel
                            val model = ArModel(
                                id = "",
                                lat = lastPosition?.first ?: 0.0,
                                lon = lastPosition?.second ?: 0.0,
                                content = text,
                                type = ArType.TEXT
                            )
                            onIntent(ArIntent.SaveComment(model))

                            // Вот — ВЫЗОВ функции createTextAnchorNode:
                            val textAnchorNode = createTextAnchorNode(
                                context = context,
                                engine = engine,
                                anchor = anchor,
                                objectModel = model,
                                windowManager = windowManager,
                                materialLoader = materialLoader
                            )
                            childNodes += textAnchorNode
                        }
                    }
                },
                onCancel = { showDialog = false }
            )
        }
    }
}

/**
 * Получаем последнее известное местоположение или, если нужно, единичный апдейт.
 */
@SuppressLint("MissingPermission")
private fun fetchLastLocation(
    client: FusedLocationProviderClient,
    onCoordinates: (latitude: Double, longitude: Double) -> Unit
) {
    client.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d("ArScreen", "Last location in button: lat=${location.latitude}, lon=${location.longitude}")
                onCoordinates(location.latitude, location.longitude)
            } else {
                // Если lastLocation == null, запрашиваем единичный апдейт
                requestOneTimeLocationUpdate(client, onCoordinates)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("debugTag", "Error getting lastLocation: ${exception.message}")
        }
}

/**
 * Запрашивает один апдейт геолокации (GPS + Wi-Fi).
 */
@SuppressLint("MissingPermission")
private fun requestOneTimeLocationUpdate(
    client: FusedLocationProviderClient,
    onCoordinates: (latitude: Double, longitude: Double) -> Unit
) {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5000L
    ).apply {
        setMaxUpdates(1)
    }.build()

    client.requestLocationUpdates(
        locationRequest,
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                client.removeLocationUpdates(this)
                val loc = locationResult.lastLocation
                if (loc != null) {
                    Log.d("debugTag", "Fresh location in button: lat=${loc.latitude}, lon=${loc.longitude}")
                    onCoordinates(loc.latitude, loc.longitude)
                }
            }
        },
        Looper.getMainLooper()
    )
}

/**
 * Создаёт AnchorNode с 3D-моделью (OBJECT).
 * anchor.name = objectModel.id (чтобы можно было ловить OnNodeClick).
 */
fun createAnchorObjectNode(
    engine: Engine,
    modelLoader: ModelLoader,
    anchor: Anchor,
    objectModel: ArModel,
    arModelPaths: List<String>
): AnchorNode {
    val anchorNode = AnchorNode(engine = engine, anchor = anchor).apply {
        name = objectModel.id
    }
    val kModelFile = arModelPaths.let {
        if (it.isNotEmpty()) it.random() else "models/shiba.glb"
    }
    val modelNode = ModelNode(
        modelInstance = modelLoader.createModelInstance(kModelFile),
        scaleToUnits = 0.5f
    )
    anchorNode.addChildNode(modelNode)
    return anchorNode
}

/**
 * Создаёт AnchorNode с текстовой 3D-меткой (ViewNode2).
 *
 * @param context       — контекст для создания Android-View (TextView).
 * @param engine        — экземпляр Filament Engine (отрисовка).
 * @param anchor        — ARCore Anchor, к которому «привязывается» текст.
 * @param objectModel   — модель, из которой берём текст (id для имени узла, content — для самого текста).
 * @param windowManager — объект WindowManager, полученный из вашего SceneView (viewNodeWindowManager).
 * @param materialLoader— материал-лоадер, полученный из вашего SceneView (rememberMaterialLoader).
 *
 * @return AnchorNode, внутри которого будет ViewNode2 с TextView.
 */
fun createTextAnchorNode(
    context: Context,
    engine: Engine,
    anchor: Anchor,
    objectModel: ArModel,
    windowManager: ViewNode2.WindowManager,
    materialLoader: MaterialLoader
): AnchorNode {
    // 1) Создаём AnchorNode на основе переданного Anchor.
    val anchorNode = AnchorNode(engine = engine, anchor = anchor).apply {
        name = objectModel.id // Чтобы можно было «ловить» клики по ID
    }

    // 2) Создаём Android-вид (TextView), в котором будет текст.
    val textView = TextView(context).apply {
        text = objectModel.content
        setTextColor(Color.White.toArgb())
        textSize = 14f
        setPadding(10, 5, 10, 5)
    }

    // 3) Создаём ViewNode2, передаём ему наш TextView + все нужные зависимости.
    val viewNode = ViewNode2(
        engine = engine,
        windowManager = windowManager,
        materialLoader = materialLoader,
        view = textView
    ).apply {
        // Убираем тени: они не нужны для плоской текстовой метки
        isShadowCaster = false
        isShadowReceiver = false

        // Задаём позицию: здесь 1 метр вверх и 2 метра «вглубь» сцены относительно Anchor.
        worldPosition = Position(x = 0f, y = 1f, z = -2f)

        // Если нужно, можно сразу заставить текст «смотреть» на камеру (billboard):
        // this.billboard = BillboardConstraint(...)  // Например, использовать billboarding
    }

    // 4) Добавляем viewNode внутрь anchorNode
    anchorNode.addChildNode(viewNode)

    return anchorNode
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
fun CommentInputDialog(onConfirm: (String) -> Unit, onCancel: () -> Unit) {
    var commentText by remember { mutableStateOf("") }
    AlertDialog(
        title = {
            Text(
                text = stringResource(id = R.string.ar_add_comment_dialog_title),
                modifier = Modifier
            )
        },
        text = {
            CustomTextInput(
                modifier = Modifier.padding(top = 24.dp),
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = stringResource(id = R.string.ar_add_comment_dialog_placeholder)
            )
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonColors(
                    containerColor = colorResource(com.blackcube.common.R.color.white),
                    contentColor = colorResource(com.blackcube.common.R.color.purple),
                    disabledContainerColor = colorResource(com.blackcube.common.R.color.white),
                    disabledContentColor = colorResource(com.blackcube.common.R.color.purple)
                ),
                content = {
                    Text(
                        text = stringResource(id = R.string.ar_add_comment_dialog_cancel),
                        modifier = Modifier
                    )
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        onConfirm(commentText.trim())
                    } else {
                        onCancel()
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonColors(
                    containerColor = colorResource(com.blackcube.common.R.color.white),
                    contentColor = colorResource(com.blackcube.common.R.color.purple),
                    disabledContainerColor = colorResource(com.blackcube.common.R.color.white),
                    disabledContentColor = colorResource(com.blackcube.common.R.color.purple)
                ),
                content = {
                    Text(
                        text = stringResource(id = R.string.ar_add_comment_dialog_confirm),
                        modifier = Modifier
                    )
                }
            )
        },
        onDismissRequest = onCancel,
        containerColor = colorResource(com.blackcube.common.R.color.white),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        shape = MaterialTheme.shapes.medium,
    )
}