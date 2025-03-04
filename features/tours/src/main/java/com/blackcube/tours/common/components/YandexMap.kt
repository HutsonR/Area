package com.blackcube.tours.common.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.MapType
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

private object MapDefaults {
    val RostovOnDon = Point(47.2357, 39.7015)
}

data class TourPoint(val id: Int, val latitude: Double, val longitude: Double, val title: String)

@Composable
fun YandexMapScreen(
    points: List<TourPoint>,
    isDarkMode: Boolean,
    onMarkerClick: (TourPoint) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    remember {
        MapKitFactory.initialize(context)
    }

    // Создаем MapView один раз и храним его
    val mapView = remember {
        MapView(context).apply {
            // Включаем жесты зума/прокрутки и пр. по необходимости (по умолчанию включены)
        }
    }

    AndroidView(factory = { mapView }) { mv ->
        mv.mapWindow.map.isNightModeEnabled = isDarkMode
        mv.mapWindow.map.mapObjects.clear()
        val mapObjects = mv.mapWindow.map.mapObjects

        points.forEachIndexed { index, point ->
            val markerBitmap = createNumberedMarkerBitmap(context, number = index + 1)
            val imageProvider = ImageProvider.fromBitmap(markerBitmap)
            val placemark = mapObjects.addPlacemark(Point(point.latitude, point.longitude), imageProvider)

            val textStyle = TextStyle().apply {
                placement = TextStyle.Placement.TOP
                offsetFromIcon = true

                if (isDarkMode) {
                    color = Color.WHITE
                    outlineColor = Color.BLACK
                } else {
                    color = Color.BLACK
                }
                size = 12F
            }
            placemark.setText(point.title, textStyle)

            placemark.addTapListener { _, _ ->
                onMarkerClick(point)
                true
            }
        }

        val targetPoint: Point = points.firstOrNull()?.let {
            Point(it.latitude, it.longitude)
        } ?: Point(MapDefaults.RostovOnDon.latitude, MapDefaults.RostovOnDon.longitude)

        mv.mapWindow.map.move(
            CameraPosition(
                targetPoint,
                14f,
                340.0f,
                30.0f
            )
        )

    }

    // Управляем lifecycle MapView (старт/стоп рендеринга карты) во время композиции
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
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
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            MapKitFactory.getInstance().onStart()
            mapView.onStart()
        }
        // При выходе из композиции снимаем наблюдателя и останавливаем карту
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }
}

// Вспомогательная функция для создания Bitmap маркера с цифрой внутри
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