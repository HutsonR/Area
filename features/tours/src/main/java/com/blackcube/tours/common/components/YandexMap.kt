package com.blackcube.tours.common.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

private object MapDefaults {
    val RostovOnDon = Point(47.2357, 39.7015)
}

data class MapPoint(val id: String, val latitude: Double, val longitude: Double, val title: String)

@Composable
fun YandexMapScreen(
    points: List<MapPoint>,
    isDarkMode: Boolean,
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
