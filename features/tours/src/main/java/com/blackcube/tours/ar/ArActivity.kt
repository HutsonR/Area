package com.blackcube.tours.ar

import android.Manifest
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.filament.Engine
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Pose
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
import io.github.sceneview.rememberView

private const val kModelFile = "models/damaged_helmet.glb"

private lateinit var locationClient: FusedLocationProviderClient
private var referenceLocation: Location? = null

class ArActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        locationClient = LocationServices.getFusedLocationProviderClient(this)

        com.blackcube.core.extension.checkPermission(
            activity = this,
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            showInContextUI = {},
            onGranted = {}
        )

        setContent {
            ARScreen()
        }
    }
}

@Composable
fun ARScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val materialLoader = rememberMaterialLoader(engine)
            val cameraNode = rememberARCameraNode(engine)
            val childNodes = rememberNodes()
            val view = rememberView(engine)
            val collisionSystem = rememberCollisionSystem(view)

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
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                onSessionUpdated = { session, updatedFrame ->
                    frame = updatedFrame

                    if (childNodes.isEmpty()) {
                        updatedFrame.getUpdatedPlanes()
                            .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                            ?.let {
                                if (referenceLocation != null) {
                                    val targetLocation = Location("").apply {
                                        latitude = 47.236757 // Пример GPS
                                        longitude = 39.706821
                                        altitude = referenceLocation?.altitude ?: 0.0
                                    }

                                    val pose = gpsToPose(targetLocation, referenceLocation!!)
                                    val anchor = session.createAnchor(pose)

                                    childNodes += createAnchorNode(
                                        engine = engine,
                                        modelLoader = modelLoader,
                                        materialLoader = materialLoader,
                                        anchor = anchor
                                    )
                                }
                            }
                    }
                }
            )
        }
}

private fun gpsToPose(location: Location, reference: Location): Pose {
    val results = FloatArray(1)

    Location.distanceBetween(
        reference.latitude, reference.longitude,
        location.latitude, reference.longitude,
        results
    )
    val north = if (location.latitude > reference.latitude) results[0] else -results[0]

    Location.distanceBetween(
        reference.latitude, reference.longitude,
        reference.latitude, location.longitude,
        results
    )
    val east = if (location.longitude > reference.longitude) results[0] else -results[0]

    val up = (location.altitude - reference.altitude).toFloat()

    return Pose(floatArrayOf(east, up, north), floatArrayOf(0f, 0f, 0f, 1f))
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

