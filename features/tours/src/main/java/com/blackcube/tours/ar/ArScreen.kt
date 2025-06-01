package com.blackcube.tours.ar

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackcube.common.utils.CollectEffect
import com.blackcube.core.navigation.AppNavigationController
import com.blackcube.tours.R
import com.blackcube.tours.ar.ArViewModel.Companion.ARGUMENT_COORDINATES
import com.blackcube.tours.ar.store.ArEffect
import com.blackcube.tours.ar.store.ArIntent
import com.blackcube.tours.ar.store.ArState
import com.blackcube.tours.ar.store.models.ArModel
import com.blackcube.tours.route.TourRouteViewModel.Companion.ARGUMENT_SELECTED_AR_COORDINATE
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Earth
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import kotlinx.coroutines.flow.Flow

@Composable
fun ArScreenRoot(
    navController: AppNavigationController,
    viewModel: ArViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect

    val savedStateFlow = navController.observeArgsAsState<List<ArModel>?>(
        key = ARGUMENT_COORDINATES,
        initial = null
    )
    val coords by savedStateFlow.collectAsState()

    LaunchedEffect(coords) {
        coords?.let {
            viewModel.setCoordinates(it)
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val materialLoader = rememberMaterialLoader(engine)
        val cameraNode = rememberARCameraNode(engine)
        val childNodes = rememberNodes()
        val view = rememberView(engine)
        val collisionSystem = rememberCollisionSystem(view)

        var earthInstance by remember { mutableStateOf<Earth?>(null) }

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
                val earth = earthInstance
                if (
                    earth != null
                    && earth.trackingState == TrackingState.TRACKING
                    && updatedFrame.camera.trackingState == TrackingState.TRACKING
                    && childNodes.isEmpty()
                    )
                {
                    val geoPose = earth.cameraGeospatialPose
                    onIntent(ArIntent.UpdateLocation(geoPose.latitude, geoPose.longitude))

                    val nearest = state.selectedArModel
                    if (state.inZone && nearest != null) {
                        Log.d("debugTag", "IN LOCATION")
                        val plane = updatedFrame
                            .getUpdatedPlanes()
                            .firstOrNull { it.trackingState == TrackingState.TRACKING
                                    && it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }

                        plane?.let {
                            Log.d("debugTag", "PLANE FOUND")
                            val anchor = it.createAnchor(it.centerPose)
                            childNodes += createAnchorNode(
                                engine = engine,
                                modelLoader = modelLoader,
                                materialLoader = materialLoader,
                                anchor = anchor,
                                pointId = nearest.id,
                                arModelPaths = state.arModelPaths
                            )
                        }
                    }

                }
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { motionEvent, node ->
                    val anchorNode = when (node) {
                        is AnchorNode -> node
                        else -> node?.parent as? AnchorNode
                    }

                    anchorNode?.name?.let {
                        onIntent(ArIntent.OnNodeClick(it))
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
    }
}

fun createAnchorNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    anchor: Anchor,
    pointId: String,
    arModelPaths: List<String>
): AnchorNode {
    val anchorNode = AnchorNode(engine = engine, anchor = anchor).apply {
        name = pointId
    }
    val kModelFile = arModelPaths.let {
        if (it.isNotEmpty()) it.random() else "models/shiba.glb"
    }
    val modelNode = ModelNode(
        modelInstance = modelLoader.createModelInstance(kModelFile),
        scaleToUnits = 0.5f
    ).apply {
        isEditable = true
        editableScaleRange = 0.2f..0.75f
    }
    val boundingBoxNode = CubeNode(
        engine,
        size = modelNode.extents,
        center = modelNode.center,
        materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
    ).apply {
        isVisible = false
    }
    modelNode.addChildNode(boundingBoxNode)
    anchorNode.addChildNode(modelNode)

    listOf(modelNode, anchorNode).forEach {
        it.onEditingChanged = { editingTransforms ->
            boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
        }
    }
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