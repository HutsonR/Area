package com.blackcube.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/**
 * `BaseViewModel` is an abstract class serving as a foundation for ViewModels in the application.
 * It provides common functionalities for managing UI state and one-time events (effects).
 *
 * @param State The type of the UI state. This should typically be a data class representing
 *              the current state of the UI.
 * @param Effects The type of the one-time events (effects). This can be a sealed class or an enum
 *                representing various actions or events that the UI needs to react to (e.g.,
 *                navigation, displaying a snackbar).
 * @param initialState The initial state of the UI. This is used to initialize the `state` flow.
 */
abstract class BaseViewModel<State, Effects>(initialState: State) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    private val _effects: MutableSharedFlow<Effects> = MutableSharedFlow(replay = 1)
    val effect: Flow<Effects> = _effects.onEach { _effects.resetReplayCache() }

    /**
     * Получение internalState
     */
    protected fun getState(): State {
        return _state.value
    }

    /**
     * Функция для копирования текущего состояния State в новое
     *
     * Example: modifyState { copy(loginError = "Ошибка!") }
     */
    protected fun modifyState(block: State.() -> State) {
        _state.update(block)
    }

    /**
     * Отправка одноразового события (эффекта)
     */
    protected fun effect(effect: Effects) {
        _effects.tryEmit(effect)
    }

}