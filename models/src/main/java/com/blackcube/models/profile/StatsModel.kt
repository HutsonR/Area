package com.blackcube.models.profile

data class StatsModel(
    val totalTours: Long,
    val userFinishedTours: Long,
    val toursPctAbove: Double,
    val totalArObjects: Long,
    val userScannedAr: Long,
    val arPctAbove: Double,
    val totalHistories: Long,
    val userDoneHistories: Long,
    val histPctAbove: Double
)