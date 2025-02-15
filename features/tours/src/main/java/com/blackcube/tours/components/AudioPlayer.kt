package com.blackcube.tours.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blackcube.tours.R

//@Composable
//fun AudioPlayer(
//    audioUrl: String,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//
//    val exoPlayer = remember {
//        ExoPlayer.Builder(context)
//            .build()
//            .apply {
//                setMediaItem(MediaItem.fromUri(audioUrl))
//                prepare()
//            }
//    }
//
//    var isPlaying by remember { mutableStateOf(false) }
//
//    DisposableEffect(exoPlayer) {
//        onDispose {
//            exoPlayer.release()
//        }
//    }
//
//    // UI: простая строка с кнопкой Play/Pause
//    Row(modifier = modifier) {
//        IconButton(
//            onClick = {
//                if (isPlaying) {
//                    exoPlayer.pause()
//                } else {
//                    exoPlayer.play()
//                }
//                isPlaying = !isPlaying
//            }
//        ) {
//            Icon(
//                imageVector = if (isPlaying) {
//                    Icons.Default.Pause
//                } else {
//                    Icons.Default.PlayArrow
//                },
//                contentDescription = if (isPlaying) "Pause" else "Play"
//            )
//        }
//        Text(
//            text = if (isPlaying) "Playing" else "Paused",
//        )
//    }
//}

//@Preview
//@Composable
//fun PreviewAudioPlayer() {
//    AudioPlayer(
//        audioUrl = ""
//    )
//}
//
//@Composable
//fun BottomPlayerTab() {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
//            .background(color = md_theme_light_primary)
//            .padding(all = 15.dp)
//    ) {
//        TrackName(trackName = selectedTrack.trackName, modifier = Modifier.weight(1f))
//        PlayPauseIcon(
//            onClick = playerEvents::onPlayPauseClick,
//        )
//    }
//}
//
//@Composable
//fun TrackName(trackName: String, modifier: Modifier) {
//    Text(
//        text = trackName,
//        style = typography.bodyLarge,
//        color = md_theme_light_onPrimary,
//        modifier = modifier.padding(start = 16.dp, end = 8.dp)
//    )
//}
//
//@Composable
//fun PlayPauseIcon(onClick: () -> Unit) {
//    if (selectedTrack.state == PlayerStates.STATE_BUFFERING) {
//        CircularProgressIndicator(
//            modifier = Modifier
//                .size(size = 48.dp)
//                .padding(all = 9.dp),
//            color = if (isBottomTab) md_theme_light_onPrimary else md_theme_light_onPrimaryContainer,
//        )
//    } else {
//        IconButton(onClick = onClick) {
//            Icon(
//                painter = painterResource(
//                    id = if (selectedTrack.state == PlayerStates.STATE_PLAYING) R.drawable.ic_pause
//                    else R.drawable.ic_play
//                ),
//                contentDescription = stringResource(id = R.string.icon_play_pause),
//                tint = if (isBottomTab) md_theme_light_onPrimary else md_theme_light_onPrimaryContainer,
//                modifier = Modifier.size(48.dp)
//            )
//        }
//    }
//}