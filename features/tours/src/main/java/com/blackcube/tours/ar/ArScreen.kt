package com.blackcube.tours.ar

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackcube.common.utils.CollectEffect
import com.blackcube.core.navigation.AppNavigationController
import com.blackcube.tours.ar.store.ArEffect
import com.blackcube.tours.ar.store.ArIntent
import com.blackcube.tours.ar.store.ArState
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Earth
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
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

private const val kModelFile = "models/damaged_helmet.glb"

@Composable
fun ArScreenRoot(
    navController: AppNavigationController,
    viewModel: ArViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effects = viewModel.effect

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
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val context = LocalContext.current

        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val materialLoader = rememberMaterialLoader(engine)
        val cameraNode = rememberARCameraNode(engine)
        val childNodes = rememberNodes()
        val view = rememberView(engine)
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
            sessionConfiguration = { session, config ->
                config.depthMode = Config.DepthMode.AUTOMATIC
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                if (session.isGeospatialModeSupported(Config.GeospatialMode.ENABLED)) {
                    config.geospatialMode = Config.GeospatialMode.ENABLED
                }
            },
            onSessionCreated = { session ->
                earthInstance = session.earth
            },
            onSessionUpdated = { session, updatedFrame ->
                frame = updatedFrame

                // 1. Проверяем геозону
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

                    if (state.inZone) {
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
                                anchor = anchor
                            )
                        }
                    }

                }
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { motionEvent, node ->
                    earthInstance?.let { earth ->
                        val geoPose = earth.cameraGeospatialPose
                        val currentLat = geoPose.latitude
                        val currentLon = geoPose.longitude
                        Log.d("debugTag", "lat $currentLat, lon $currentLon")
                        showToast(context, "lat $currentLat, lon $currentLon")
                    }
                })
        )
    }
}

fun createAnchorNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    anchor: Anchor
): AnchorNode {
    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
    val modelNode = ModelNode(
        modelInstance = modelLoader.createModelInstance(kModelFile),
        // Scale to fit in a 0.5 meters cube
        scaleToUnits = 0.5f
    ).apply {
        // Model Node needs to be editable for independent rotation from the anchor rotation
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

private fun showToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

