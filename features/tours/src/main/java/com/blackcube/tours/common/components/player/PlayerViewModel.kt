package com.blackcube.tours.common.components.player

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.blackcube.tours.common.models.PlayerState
import com.blackcube.tours.common.models.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val defaultTrack = Track(
        name = "Default",
        durationMillis = 0F,
        url = "https://example.com/audio.mp3"
    )

    private val _currentTrack = MutableStateFlow(defaultTrack)
    val currentTrack: StateFlow<Track> = _currentTrack.asStateFlow()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition.asStateFlow()

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(defaultTrack.url))
        prepare()
    }

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlayerViewModel", "Player error: ${error.message}", error)
            }

            override fun onPlaybackStateChanged(state: Int) {
                _playerState.value = when (state) {
                    Player.STATE_BUFFERING -> PlayerState.Buffering
                    Player.STATE_READY -> {
                        val durationMillis = exoPlayer.duration
                        if (durationMillis > 0 && _currentTrack.value.durationMillis.toLong() != durationMillis) {
                            _currentTrack.value = _currentTrack.value.copy(durationMillis = durationMillis.toFloat())
                        }
                        if (exoPlayer.isPlaying) PlayerState.Playing else PlayerState.Paused
                    }
                    else -> PlayerState.Idle
                }
            }
        })

        viewModelScope.launch {
            while (true) {
                if (exoPlayer.isPlaying) {
                    _playbackPosition.value = exoPlayer.currentPosition
                }
                delay(1000L)
            }
        }
    }

    fun setTrack(newTrack: Track) {
        if (_currentTrack.value != newTrack) {
            _currentTrack.value = newTrack
            exoPlayer.setMediaItem(MediaItem.fromUri(newTrack.url))
            exoPlayer.prepare()
            _playbackPosition.value = 0L
        }
    }

    fun changePosition(newPos: Long) {
        exoPlayer.seekTo(newPos)
        _playbackPosition.value = exoPlayer.currentPosition
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            _playerState.value = PlayerState.Paused
        } else {
            exoPlayer.play()
            _playerState.value = PlayerState.Playing
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
