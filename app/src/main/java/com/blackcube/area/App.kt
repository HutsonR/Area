package com.blackcube.area

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey("2900c408-49a4-47ad-8437-78227fc91332")
    }
}