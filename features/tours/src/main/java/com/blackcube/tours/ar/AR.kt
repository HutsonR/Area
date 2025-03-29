package com.blackcube.tours.ar

import android.app.Activity
import android.app.ActivityManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commitNow
import androidx.navigation.NavController
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.Objects

@Composable
fun ArScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    // Генерируем уникальный ID для контейнера фрагмента
    val containerId = remember { androidx.core.view.ViewCompat.generateViewId() }

    // Размещаем FragmentContainerView через AndroidView
    AndroidView(factory = { ctx ->
        androidx.fragment.app.FragmentContainerView(ctx).apply {
            id = containerId
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
    })

    // Добавляем ArFragment в контейнер, если он ещё не добавлен
    LaunchedEffect(containerId) {
        (ArFragment() as? Fragment)?.let { fragment ->
            activity?.supportFragmentManager?.commitNow {
                if (activity.supportFragmentManager.findFragmentById(containerId) == null) {
                    add(containerId, fragment, "arFragment")
                }
            }
        }

        // Получаем фрагмент по id, поскольку транзакция выполнена синхронно
        val arFragment = activity?.supportFragmentManager?.findFragmentById(containerId) as? ArFragment

        arFragment?.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            // Пример: загрузка модели при нажатии
            val anchor = hitResult.createAnchor()
            ModelRenderable.builder()
                .setSource(context, com.blackcube.tours.R.raw.robot)
                .build()
                .thenAccept { modelRenderable ->
                    addModel(arFragment, anchor, modelRenderable)
                }
                .exceptionally { throwable ->
                    // Вывод сообщения об ошибке (например, через Toast или AlertDialog)
                    null
                }
        }
    }
}

// Метод для добавления модели в сцену AR
private fun addModel(arFragment: ArFragment, anchor: Anchor, modelRenderable: ModelRenderable) {
    val anchorNode = AnchorNode(anchor).apply {
        setParent(arFragment.arSceneView.scene)
    }
    TransformableNode(arFragment.transformationSystem).apply {
        setParent(anchorNode)
        renderable = modelRenderable
        select()
    }
}

private fun checkSystemSupport(activity: Activity): Boolean {
    val openGlVersion = (Objects.requireNonNull(
        activity.getSystemService(Activity.ACTIVITY_SERVICE)
    ) as ActivityManager).deviceConfigurationInfo.glEsVersion
    if (openGlVersion.toDouble() >= 3.0) {
        return true
    } else {
        Toast.makeText(activity, "Приложению требуется OpenGL ES 3.0 или выше", Toast.LENGTH_SHORT).show()
        return false
    }
}