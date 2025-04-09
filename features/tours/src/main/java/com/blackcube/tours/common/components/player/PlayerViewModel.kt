package com.blackcube.tours.common.components.player

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.blackcube.remote.models.tts.TtsApiModel
import com.blackcube.remote.repository.tts.TtsRepository
import com.blackcube.remote.repository.tts.TtsRepositoryImpl.Companion.DEFAULT_TTS_MODEL_ID
import com.blackcube.tours.common.models.PlayerState
import com.blackcube.tours.common.models.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val ttsRepository: TtsRepository
) : ViewModel() {

    private val _currentTrack = MutableStateFlow(TrackState())
    val currentTrack: StateFlow<TrackState> = _currentTrack.asStateFlow()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition.asStateFlow()

    private var hasError: Boolean = false

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(TrackState().text))
        prepare()
    }

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                // При ошибке воспроизведения устанавливаем состояние ошибки
                Log.e("PlayerViewModel", "Player error: ${error.message}", error)
                _playerState.value = PlayerState.Error(error.message ?: "Ошибка воспроизведения")
                hasError = true
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (hasError) return

                when (state) {
                    Player.STATE_BUFFERING -> _playerState.value = PlayerState.Buffering
                    Player.STATE_READY -> {
                        val durationMillis = exoPlayer.duration
                        if (durationMillis <= 1000L && _currentTrack.value.durationMillis > 1000) {
                            _playerState.value = PlayerState.Error("Ошибка получения файла аудио")
                        } else {
                            if (durationMillis > 0 && _currentTrack.value.durationMillis.toLong() != durationMillis) {
                                _currentTrack.value = _currentTrack.value.copy(durationMillis = durationMillis.toFloat())
                            }
                            _playerState.value = if (exoPlayer.isPlaying) PlayerState.Playing else PlayerState.Paused
                        }
                    }
                    Player.STATE_ENDED -> {
                        _playbackPosition.value = exoPlayer.duration
                        _playerState.value = PlayerState.Paused
                    }
                    else -> _playerState.value = PlayerState.Idle
                }
            }
        })

        viewModelScope.launch {
            while (true) {
                if (exoPlayer.isPlaying) {
                    _playbackPosition.value = exoPlayer.currentPosition
                }
                delay(100L)
            }
        }
    }

    /**
     * Устанавливает новый трек для воспроизведения.
     *
     * @param newTrack Новый трек, который необходимо воспроизвести.
     * @param context Контекст приложения, необходимый для доступа к временным файлам.
     */
    fun setTrack(newTrack: Track, context: Context) {
        viewModelScope.launch {
            _playerState.value = PlayerState.Buffering
            _currentTrack.value = TrackState(
                name = newTrack.name,
                text = newTrack.text,
                durationMillis = newTrack.durationMillis
            )
            val url = getTrackUrl(newTrack.text, context)
            if (!url.isNullOrEmpty()) {
                hasError = false
                exoPlayer.setMediaItem(MediaItem.fromUri(url))
                exoPlayer.prepare()
                _playbackPosition.value = 0L
            } else {
                _playerState.value = PlayerState.Error("Ошибка получения файла аудио")
                hasError = true
            }
        }
    }

    /**
     * Асинхронно получает URL для воспроизведения, конвертируя текст в речь через TTS.
     * Аудиоданные сохраняются во временный файл.
     *
     * @param text Текст для конвертации.
     * @param context Контекст для доступа к cacheDir.
     * @return Абсолютный путь к временно созданному аудиофайлу или null при ошибке.
     */
    private suspend fun getTrackUrl(text: String, context: Context): String? {
        val audioBytes = ttsRepository.convertTextToSpeech(
            ttsApiModel = TtsApiModel(
                text = text,
                modelId = DEFAULT_TTS_MODEL_ID
            )
        )
        return audioBytes?.let { bytes ->
            try {
                val tempFile = File(context.cacheDir, "speech_output.mp3")
                tempFile.writeBytes(bytes)
                tempFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Изменяет позицию воспроизведения.
     *
     * @param newPos Новая позиция в миллисекундах.
     */
    fun changePosition(newPos: Long) {
        exoPlayer.seekTo(newPos)
        _playbackPosition.value = exoPlayer.currentPosition
    }

    /**
     * Переключает состояние воспроизведения: если плеер играет – ставит на паузу, иначе – начинает воспроизведение.
     */
    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            _playerState.value = PlayerState.Paused
        } else {
            if (exoPlayer.playbackState == Player.STATE_ENDED) {
                changePosition(0)
            }
            if (hasError) {
                hasError = false
            }
            exoPlayer.play()
            _playerState.value = PlayerState.Playing
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }

    /**
     * Данные состояния текущего трека.
     *
     * @property name Название трека.
     * @property text Текст трека.
     * @property durationMillis Длительность трека в миллисекундах.
     */
    data class TrackState(
        val name: String = "",
        val text: String = "",
        val durationMillis: Float = 0F,
    )
}
