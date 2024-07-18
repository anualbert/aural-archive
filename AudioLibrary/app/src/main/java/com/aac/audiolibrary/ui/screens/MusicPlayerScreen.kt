package com.aac.audiolibrary.ui.screens

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.aac.audiolibrary.R
import com.aac.audiolibrary.database.Song
import com.aac.audiolibrary.viewmodels.SongViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MusicPlayerScreen(songViewModel: SongViewModel) {
    var songTitle by remember { mutableStateOf("") }
    var songArtist by remember { mutableStateOf("") }
    val songs = remember { mutableStateListOf<Song>() }
    var songName by remember {
        mutableStateOf("")
    }
    var currentSong by remember { mutableStateOf(Song(data = "")) }
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    var currentSongIndex by remember {
        mutableIntStateOf(0)
    }
    var songDuration by remember {
        mutableFloatStateOf(0f)
    }
    val scope = rememberCoroutineScope()
    var currentTime = remember {
        mutableFloatStateOf(0f)
    }
    var isPlaying by remember { mutableStateOf(false) }

    var setMediaItem = {
        currentSong = songs[currentSongIndex]
        exoPlayer.setMediaItem(MediaItem.fromUri(currentSong.data))
        exoPlayer.prepare()
        songName = currentSong.title.toString()
        currentSong.duration?.toFloat()?.let {
            songDuration = it
        }
    }

    LaunchedEffect(Unit) {
        var songList: List<Song>
        withContext(Dispatchers.IO) {
            songList = getSongsFromDevice(context)
        }
        val mediaItem =
            MediaItem.fromUri("https://audio-edge-hy4wy.blr.d.radiomast.io/ref-128k-mp3-stereo")
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        songs.addAll(songList)
    }

    LaunchedEffect(key1 = isPlaying) {
        scope.launch {
            while (exoPlayer.isPlaying) {
                Log.d("amal", "MusicPlayerScreen: ")
                currentTime.floatValue = exoPlayer.currentPosition.toFloat()
                    ?: 0f
                delay(1000L)
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.8f)
                .background(color = Color.White, shape = RoundedCornerShape(3.dp))
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(3.dp))
                .padding(10.dp)
        ) {
            items(songs) { song ->
                Spacer(modifier = Modifier.height(5.dp))
                Column(Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = Color.Gray)
                ) {
                    currentTime.floatValue = 0f
                    currentSong = song
                    currentSong.duration?.toFloat()?.let {
                        songDuration = it
                    }
                    currentSongIndex = songs.indexOf(currentSong)
                    exoPlayer.setMediaItem(MediaItem.fromUri(song.data))
                    songName = song.title.toString()
                    isPlaying = exoPlayer.isPlaying
                }) {
                    Text(text = "${song.title} - ${song.artist}")
                }
                Spacer(modifier = Modifier.height(5.dp))
                Divider()
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height((screenHeight / 7).dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Red.copy(alpha = .7f),
                            Color.Red.copy(alpha = .8f),
                            Color.Red.copy(alpha = .9f),
                            Color.Red,
                            Color.Red.copy(alpha = .9f),
                            Color.Red.copy(alpha = .8f),
                            Color.Red.copy(alpha = .7f)

                        )
                    ),
                    shape = RoundedCornerShape(3.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = songName)
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Image(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) {
                        if (currentSongIndex > 0) {
                            currentSongIndex--
                            setMediaItem()
                        }
                    },
                    painter =
                    painterResource(id = R.drawable.ic_prev),
                    alpha = if (currentSongIndex == 0) 0.5f else 1.0f,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) {
                        if (exoPlayer.playbackState == Player.STATE_READY) {
                            if (isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        }
                        isPlaying = exoPlayer.isPlaying


                    },
                    painter =
                    if (!isPlaying)
                        painterResource(id = R.drawable.ic_play)
                    else
                        painterResource(id = R.drawable.ic_pause),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) {
                        if (currentSongIndex < songs.lastIndex) {
                            currentSongIndex++
                            setMediaItem()
                        }
                    },
                    painter =
                    painterResource(id = R.drawable.ic_next),
                    alpha = if (currentSongIndex == songs.lastIndex) 0.5f else 1.0f,
                    contentDescription = ""
                )
            }
            TimeBar(currentTime, songDuration) { newPosition ->
                if (newPosition == songDuration) {
                    currentTime.floatValue = 0f
                }
//                exoPlayer.seekTo(newPosition.toLong())
            }
        }
    }
}

@Composable
fun TimeBar(currentPosition: MutableFloatState, duration: Float, onSeek: (Float) -> Unit) {
    val timeval = fun(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${(currentPosition.floatValue / 1000).toInt()}s",
                color = Color.White
            )
            Spacer(modifier = Modifier.width(5.dp))
            Slider(
                value = currentPosition.floatValue,
                onValueChange = onSeek,
                valueRange = 0f..duration,
                modifier = Modifier.fillMaxWidth(.8f)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = timeval(duration.toLong()),
                color = Color.White
            )
        }
    }
}

fun getSongsFromDevice(context: Context): List<Song> {
    val songList = mutableListOf<Song>()
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA
    )
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        null,
        null
    )
    Log.d("amal", "getSongsFromDevice:cursor ${cursor?.count} ")
    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            val title = it.getString(titleColumn)
            val artist = it.getString(artistColumn)
            val duration = it.getLong(durationColumn)
            val data = it.getString(dataColumn)
            val song =
                Song(id = 0, title = title, artist = artist, duration = duration, data = data)
            songList.add(song)
        }
    }
    Log.d("amal", "getSongsFromDevice: ${songList.count()}")
    return songList
}

//@Composable
//fun PreviewSongListScreen() {
//    MusicPlayerScreen()
//}
