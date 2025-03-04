package com.blackcube.tours.common.models

sealed class PlayerState {
    data object Idle : PlayerState()
    data object Buffering : PlayerState()
    data object Playing : PlayerState()
    data object Paused : PlayerState()
    data class Error(val message: String) : PlayerState()
}
