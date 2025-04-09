package com.blackcube.tours.common.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.twotone.SkipNext
import androidx.compose.material.icons.twotone.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.blackcube.tours.common.models.PlayerState
import com.blackcube.tours.common.models.Track

fun formatTime(millis: Float): String {
    val totalSeconds = (millis / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
fun AudioPlayer(
    track: Track,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Отдельное состояние для ползунка, которое обновляется автоматически при изменении позиции воспроизведения.
    var sliderValue by remember { mutableStateOf(0L) }
    val playbackPosition by viewModel.playbackPosition.collectAsState()

    LaunchedEffect(playbackPosition) {
        sliderValue = playbackPosition
    }

    // При изменении трека вызывается setTrack для его установки
    LaunchedEffect(track) {
        viewModel.setTrack(track, context)
    }

    val currentTrack by viewModel.currentTrack.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = com.blackcube.common.R.color.player_background))
            .padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            AudioTrackName(trackName = currentTrack.name)
            AudioSliderBar(
                value = sliderValue.toFloat(),
                onValueChange = { sliderValue = it.toLong() },
                onValueChangeFinished = { viewModel.changePosition(sliderValue) },
                songDuration = currentTrack.durationMillis
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(sliderValue.toFloat()),
                    fontSize = 14.sp,
                    color = colorResource(id = com.blackcube.common.R.color.title_color)
                )
                Text(
                    text = formatTime(currentTrack.durationMillis),
                    fontSize = 14.sp,
                    color = colorResource(id = com.blackcube.common.R.color.title_color)
                )
            }
        }
        AudioPlayPauseButton(
            state = playerState,
            onClick = {
                if (playerState !is PlayerState.Error) {
                    viewModel.playPause()
                }
            }
        )
    }
}

@Composable
fun AudioSliderBar(
    value: Float,
    onValueChange: (newValue: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    songDuration: Float
) {
    Slider(
        value = value,
        onValueChange = { onValueChange(it) },
        onValueChangeFinished = { onValueChangeFinished() },
        valueRange = 0f..songDuration,
        colors = SliderDefaults.colors(
            thumbColor = colorResource(id = com.blackcube.common.R.color.progress_gray),
            activeTrackColor = colorResource(id = com.blackcube.common.R.color.progress_gray),
            inactiveTrackColor = colorResource(id = com.blackcube.common.R.color.progress_background_gray)
        )
    )
}

@Composable
fun AudioTrackName(
    trackName: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(end = 8.dp),
        text = trackName,
        fontSize = 16.sp,
        color = colorResource(id = com.blackcube.common.R.color.title_color),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun AudioPlayPauseButton(
    state: PlayerState,
    onClick: () -> Unit
) {
    if (state is PlayerState.Buffering) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(32.dp)
                .padding(4.dp)
        )
    } else {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = when (state) {
                    is PlayerState.Playing -> Icons.Default.Pause
                    is PlayerState.Error -> Icons.Default.ErrorOutline
                    else -> Icons.Default.PlayArrow
                },
                contentDescription = "Play/Pause Button",
                tint = colorResource(id = com.blackcube.common.R.color.black),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun AudioControlButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = "Control Button",
            tint = colorResource(id = com.blackcube.common.R.color.black),
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
@Preview
fun PreviewTrackName() {
    AudioTrackName("Какой-то трек")
}

@Composable
@Preview
fun PreviewPlayPauseButton() {
    Row {
        AudioPlayPauseButton(PlayerState.Buffering) {}
        AudioPlayPauseButton(PlayerState.Playing) {}
        AudioPlayPauseButton(PlayerState.Paused) {}
    }
}

@Composable
@Preview
fun PreviewAudioControlButton() {
    Row {
        AudioControlButton(icon = Icons.TwoTone.SkipNext) {}
        AudioControlButton(icon = Icons.TwoTone.SkipPrevious) {}
    }
}