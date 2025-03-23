//package com.blackcube.tours.ar
//
//import androidx.appcompat.app.AppCompatActivity
//import androidx.transition.Scene
//import com.blackcube.tours.R
//import com.google.ar.core.AugmentedImage
//import com.google.ar.core.TrackingState
//import com.google.ar.sceneform.AnchorNode
//import com.google.ar.sceneform.FrameTime
//import com.google.ar.sceneform.rendering.ModelRenderable
//import com.yandex.mapkit.directions.driving.Landmark
//import com.yandex.mapkit.location.Location
//import com.yandex.mapkit.mapview.MapView
//import com.yandex.runtime.image.ImageProvider
//
//class ARMapActivity : AppCompatActivity(), Scene.OnUpdateListener {
//
//    private lateinit var arFragment: CustomArFragment  // наш AR фрагмент с кастомной конфигурацией
//    private lateinit var mapView: MapView
//    private val modelRenderables = mutableMapOf<String, ModelRenderable>()
//    private val recognizedImages = mutableSetOf<AugmentedImage>()
//    private val landmarksMap = mutableMapOf<String, Landmark>()
//    private var currentLocation: Location? = null
//    private var currentAzimuth: Double? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_armap)  // в разметке должны быть ArFragment и MapView
//
//        // Инициализация AR-фрагмента
//        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as CustomArFragment
//        arFragment.arSceneView.scene.addOnUpdateListener(this)  // слушатель обновлений сцены
//
//        // Инициализация карты
//        mapView = findViewById(R.id.map_view)
//        mapView.map.isScrollGesturesEnabled = true
//
//        // Получаем текущую локацию пользователя (используем простое API LocationServices или MapKit)
//        // Здесь должен быть код для запроса разрешения LOCATION и получения координат.
//        currentLocation = getLastKnownLocation() // псевдо-функция, замените реальным получением локации
//        currentAzimuth = getDeviceAzimuth()      // псевдо-функция, замените реализацией компаса
//
//        // Центрируем карту на пользователе
//        currentLocation?.let { loc ->
//            val userPoint = Point(loc.latitude, loc.longitude)
//            mapView.map.move(CameraPosition(userPoint, 16.0f, 0.0f, 0.0f))
//        }
//
//        // Загрузка 3D моделей
//        loadModels()
//
//        // Получение данных из Firebase и размещение объектов
//        currentLocation?.let { loc ->
//            fetchNearbyLandmarks(loc, radiusMeters = 1000.0)  // например, ищем в радиусе 1 км
//        }
//    }
//
//    override fun onUpdate(frameTime: FrameTime) {
//        // Обработка распознавания Augmented Images (например, снежинки)
//        val frame = arFragment.arSceneView.arFrame ?: return
//        val updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
//        for (augImage in updatedAugmentedImages) {
//            if (augImage.trackingState == TrackingState.TRACKING && !recognizedImages.contains(augImage)) {
//                // Проверяем, является ли распознанное изображение нашим маркером
//                val imageName = augImage.name  // имя, присвоенное в базе AugmentedImages
//                if (imageName == "SnowflakeMarker") {
//                    recognizedImages.add(augImage)
//                    // Создаем якорь и привязываем модель снежинки
//                    val anchor = augImage.createAnchor(augImage.centerPose)
//                    val anchorNode = AnchorNode(anchor).apply {
//                        setParent(arFragment.arSceneView.scene)
//                    }
//                    modelRenderables["snowflake.glb"]?.let { model ->
//                        anchorNode.renderable = model
//                    }
//                    // Вызываем колбэк распознавания снежинки
//                    val snowflakeObj = RecognizedObject(id="snowflake1", name="Новогодняя снежинка", lat=null, lon=null)
//                    onObjectRecognized(snowflakeObj)
//                }
//            }
//        }
//    }
//
//    /** Загрузка GLB моделей в память */
//    private fun loadModels() {
//        val models = listOf("snowflake.glb", "spasskaya_tower.glb", "other_monument.glb")
//        for (file in models) {
//            val uri = Uri.parse("models/$file")
//            ModelRenderable.builder()
//                .setSource(this, RenderableSource.builder().setSource(this, uri, RenderableSource.SourceType.GLB)
//                    .setRecenterMode(RenderableSource.RecenterMode.ROOT).build())
//                .setRegistryId(file)
//                .build()
//                .thenAccept { renderable -> modelRenderables[file] = renderable }
//                .exceptionally { ex ->
//                    Log.e("AR", "Не удалось загрузить модель $file: ${ex.message}")
//                    null
//                }
//        }
//    }
//
//    /** Запрос данных Firebase и обработка ближайших объектов */
//    private fun fetchNearbyLandmarks(userLocation: Location, radiusMeters: Double) {
//        val db = Firebase.firestore
//        db.collection("landmarks").get()
//            .addOnSuccessListener { result ->
//                for (doc in result) {
//                    val landmark = doc.toObject(Landmark::class.java)
//                    landmarksMap[landmark.id] = landmark
//                    // Добавляем метку на карту
//                    addMarkerToMap(landmark)
//                    // Проверяем расстояние до пользователя
//                    val dist = distanceBetween(userLocation.latitude, userLocation.longitude, landmark.lat, landmark.lon)
//                    if (dist <= radiusMeters) {
//                        // Размещаем 3D-модель объекта в AR
//                        placeLandmarkInAR(userLocation, landmark)
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firebase", "Ошибка при получении landmarks: ${e.message}")
//            }
//    }
//
//    /** Разместить объект (здание/памятник) в AR-сцене по координатам */
//    private fun placeLandmarkInAR(userLocation: Location, landmark: Landmark) {
//        // Вычисляем позу (координаты) относительно пользователя
//        val azimuth = currentAzimuth ?: 0.0
//        val pose = computeRelativePose(userLocation, azimuth, landmark.lat, landmark.lon)
//        val anchor = arFragment.arSceneView.session?.createAnchor(pose) ?: return
//        val anchorNode = AnchorNode(anchor).apply {
//            setParent(arFragment.arSceneView.scene)
//        }
//        // Назначаем 3D модель (если не загружена конкретная, можно поставить общую метку)
//        val model = modelRenderables[landmark.modelName] ?: modelRenderables["default.glb"]
//        model?.let { anchorNode.renderable = it }
//        // Вызов колбэка о распознавании объекта
//        val recognizedObj = RecognizedObject(id = landmark.id, name = landmark.name, lat = landmark.lat, lon = landmark.lon)
//        onObjectRecognized(recognizedObj)
//    }
//
//    /** Добавить метку объекта на карту */
//    private fun addMarkerToMap(landmark: Landmark) {
//        val mapObjects = mapView.map.mapObjects
//        val point = Point(landmark.lat, landmark.lon)
//        mapObjects.addPlacemark(point, ImageProvider.fromResource(this, R.drawable.map_marker))
//    }
//
//    /** Обработка события распознавания объекта */
//    private fun onObjectRecognized(obj: RecognizedObject) {
//        // Здесь можно реализовать логику при распознавании: показать информацию и т.д.
//        Log.d("AR", "Распознан объект: ${obj.name} (id=${obj.id})")
//        Toast.makeText(this, "Распознан: ${obj.name}", Toast.LENGTH_SHORT).show()
//    }
//
//    /** Вычисление Pose на основе гео-координат */
//    private fun computeRelativePose(userLoc: Location, userAzimuthDeg: Double, targetLat: Double, targetLon: Double): Pose {
//        val dLat = Math.toRadians(targetLat - userLoc.latitude)
//        val dLon = Math.toRadians(targetLon - userLoc.longitude)
//        val earthR = 6371000.0
//        val northOffset = dLat * earthR
//        val eastOffset = dLon * earthR * kotlin.math.cos(Math.toRadians(userLoc.latitude))
//        // Поворот с учётом азимута (по часовой стрелке)
//        val azRad = Math.toRadians(userAzimuthDeg)
//        val localX =  eastOffset * kotlin.math.cos(-azRad) - northOffset * kotlin.math.sin(-azRad)
//        val localZ =  eastOffset * kotlin.math.sin(-azRad) + northOffset * kotlin.math.cos(-azRad)
//        val translation = floatArrayOf(localX.toFloat(), 0f, localZ.toFloat())
//        val rotation = floatArrayOf(0f, 0f, 0f, 1f)
//        return Pose(translation, rotation)
//    }
//
//    // ... Методы получения currentLocation и currentAzimuth опущены (они должны использовать LocationManager/SensorManager).
//}
