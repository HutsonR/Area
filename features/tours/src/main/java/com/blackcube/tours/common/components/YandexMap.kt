package com.blackcube.tours.common.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.FitnessOptions
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.RouteOptions
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider


private object MapDefaults {
    val RostovOnDon = Point(47.2357, 39.7015)
}

data class MapPoint(val id: String, val latitude: Double, val longitude: Double, val title: String)

/**
 * A composable function that displays a Yandex Map with markers.
 *
 * @param points A list of [MapPoint] objects representing the locations to display on the map.
 * @param isDarkMode A boolean indicating whether to use dark mode for the map.
 * @param onMarkerClick A lambda function that is called when a marker on the map is clicked.
 *                      It receives the clicked [MapPoint] as a parameter.
 *
 * This function initializes the Yandex MapKit, creates a [MapView], adds placemarks (markers)
 * for each point in the `points` list, and sets up listeners for camera movement and marker clicks.
 * It also handles the display of labels based on the zoom level, showing them when zoomed in
 * (zoom >= 14) and hiding them when zoomed out (zoom < 14).
 *
 * Lifecycle management:
 * - It ensures proper initialization and cleanup of the MapKit based on the lifecycle of the
 *   Composable, starting and stopping the map when the lifecycle owner is started or stopped.
 * - It also listens to the camera position changes to dynamically change text labels visibility
 *
 * Features:
 * - Displays multiple markers on the map.
 * - Supports dark mode. */
@Composable
fun YandexMapScreen(
    points: List<MapPoint>,
    isDarkMode: Boolean,
    moveToLocation: Point? = null,
    buildRoute: Boolean = false,
    onMarkerClick: (MapPoint) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    remember {
        MapKitFactory.initialize(context)
    }

    val mapView = remember { MapView(context) }

    val placemarkList = remember { mutableListOf<com.yandex.mapkit.map.PlacemarkMapObject>() }
    val placemarkTapListeners = remember { mutableListOf<MapObjectTapListener>() }

    var labelsVisible by remember { mutableStateOf(true) }

    fun showLabels(textStyle: TextStyle) {
        placemarkList.forEach { placemark ->
            val pointData = placemark.userData as? MapPoint
            pointData?.let {
                placemark.setText(it.title, textStyle)
            }
        }
        labelsVisible = true
    }

    fun hideLabels(textStyle: TextStyle) {
        placemarkList.forEach { placemark ->
            placemark.setText("", textStyle)
        }
        labelsVisible = false
    }

    // Слушатель камеры для отслеживания изменения зума
    val cameraListener = remember {
        object : CameraListener {
            var textStyle: TextStyle = TextStyle()
            override fun onCameraPositionChanged(
                map: com.yandex.mapkit.map.Map,
                cameraPosition: CameraPosition,
                cameraUpdateReason: com.yandex.mapkit.map.CameraUpdateReason,
                finished: Boolean
            ) {
                if (finished) {
                    val zoom = cameraPosition.zoom
                    if (zoom >= 14f && !labelsVisible) {
                        showLabels(textStyle)
                    } else if (zoom < 14f && labelsVisible) {
                        hideLabels(textStyle)
                    }
                }
            }
        }
    }

    AndroidView(factory = { mapView }) { mv ->
        mv.mapWindow.map.isNightModeEnabled = isDarkMode

        val mapKit = MapKitFactory.getInstance()
        mapKit.createUserLocationLayer(mv.mapWindow).apply {
            isVisible = true
            isHeadingEnabled = true
        }

        mv.mapWindow.map.poiLimit = 0

        mv.mapWindow.map.mapObjects.clear()
        placemarkList.clear()
        placemarkTapListeners.clear()

        val textStyle = TextStyle().apply {
            placement = TextStyle.Placement.TOP
            offsetFromIcon = true
            color = if (isDarkMode) Color.WHITE else Color.BLACK
            if (isDarkMode) {
                outlineColor = Color.BLACK
            }
            size = 12f
        }
        cameraListener.textStyle = textStyle

        // Добавляем метки на карту
        val mapObjects = mv.mapWindow.map.mapObjects
        points.forEachIndexed { index, point ->
            val markerBitmap = createNumberedMarkerBitmap(context, number = index + 1)
            val imageProvider = ImageProvider.fromBitmap(markerBitmap)
            val placemark = mapObjects.addPlacemark(Point(point.latitude, point.longitude), imageProvider)
            placemark.userData = point
            placemark.setText(point.title, textStyle)

            val tapListener = MapObjectTapListener { mapObject, p ->
                onMarkerClick(point)
                true
            }
            placemark.addTapListener(tapListener)
            placemarkList.add(placemark)
            placemarkTapListeners.add(tapListener)
        }

        val targetPoint = points.firstOrNull()?.let {
            Point(it.latitude, it.longitude)
        } ?: MapDefaults.RostovOnDon
        mv.mapWindow.map.move(CameraPosition(targetPoint, 13f, 340.0f, 30.0f))
    }

    LaunchedEffect(moveToLocation) {
        moveToLocation?.let { location ->
            mapView.mapWindow.map.move(
                CameraPosition(location, 16f, 340.0f, 30.0f),
                Animation(Animation.Type.SMOOTH, 1.0f),
                null
            )
        }
    }

    fun drawRoute(route: Route) {
        val mapObjects = mapView.mapWindow.map.mapObjects
        route.sections.forEach { section ->
            val polyline = SubpolylineHelper.subpolyline(route.geometry, section.geometry)
            mapObjects.addPolyline(polyline)
        }
    }

    val routeListener = object : Session.RouteListener {
        override fun onMasstransitRoutes(routes: MutableList<Route>) {
            Log.e("YandexMap", "Маршрут построен успешно")
            if (routes.isNotEmpty()) {
                drawRoute(routes[0])
            }
        }

        override fun onMasstransitRoutesError(error: Error) {
            Log.e("YandexMap", "Ошибка построения маршрута: ${error.javaClass}")
        }
    }

    LaunchedEffect(buildRoute) {
        try {
            if (buildRoute && points.size >= 2) {
                val router = TransportFactory.getInstance().createPedestrianRouter()
                val timeOptions = TimeOptions()
                val routeOptions = RouteOptions(
                    FitnessOptions()
                )

                val requestPoints = points.map { point ->
                    RequestPoint(
                        Point(point.latitude, point.longitude),
                        RequestPointType.WAYPOINT,
                        null,
                        null,
                        null
                    )
                }
                router.requestRoutes(requestPoints, timeOptions, routeOptions, routeListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Подписываемся на события жизненного цикла для корректной работы карты
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        mapView.mapWindow.map.addCameraListener(cameraListener)

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                MapKitFactory.getInstance().onStart()
                mapView.onStart()
            }
            if (event == Lifecycle.Event.ON_STOP) {
                mapView.onStop()
                MapKitFactory.getInstance().onStop()
            }
        }

        lifecycle.addObserver(observer)
        // Если Lifecycle уже запущен, запускаем карту сразу
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            MapKitFactory.getInstance().onStart()
            mapView.onStart()
        }

        onDispose {
            mapView.mapWindow.map.removeCameraListener(cameraListener)
            lifecycle.removeObserver(observer)
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }
}

/**
 * Creates a Bitmap representing a numbered marker.
 *
 * This function generates a circular bitmap with a specified number displayed in the center.
 *
 * @param context The application context, used for accessing resources like display metrics.
 * @param number The number to be displayed on the marker.
 * @return A Bitmap object representing the numbered marker.
 */
fun createNumberedMarkerBitmap(context: Context, number: Int): Bitmap {
    val sizeDp = 36
    val px = (sizeDp * context.resources.displayMetrics.density).toInt()
    val bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(0, 150, 255)
        style = Paint.Style.FILL
    }
    val radius = px / 2f
    canvas.drawCircle(radius, radius, radius, circlePaint)

    val text = number.toString()
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = px * 0.5f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    val textX = radius
    val textY = radius - ((textPaint.descent() + textPaint.ascent()) / 2)
    canvas.drawText(text, textX, textY, textPaint)

    return bitmap
}