package com.alashow.datmusic.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alashow.datmusic.data.AlbumsDataProvider
import com.alashow.datmusic.data.RadioDataProvider
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.alashow.common.compose.LocalPlaybackConnection
import com.alashow.datmusic.data.Radio
import com.alashow.datmusic.data.RadioList
import com.alashow.datmusic.playback.PlaybackConnection
import com.alashow.datmusic.ui.library.R
import com.google.gson.Gson


@Composable
fun getRadiosList(): List<Radio> {
    val context = LocalContext.current

    return remember(context) {
        val content = context.resources.openRawResource(R.raw.radios)
            .bufferedReader()
            .use { it.readText() }
        Gson().fromJson(content, RadioList::class.java).radios
    }
}


@Composable
fun RadioHome (
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current
) {
    val scrollState = rememberScrollState(0)
    val surfaceGradient = RadioDataProvider.radioSurfaceGradient(isSystemInDarkTheme())
    val radioList = getRadiosList()
    Box(modifier = Modifier.fillMaxSize()) {
        ScrollableContent(scrollState = scrollState,
            surfaceGradient = surfaceGradient,
            playbackConnection = playbackConnection,
            radioList = radioList)
    }
}

fun Modifier.horizontalGradientBackground(
    colors: List<Color>
) = gradientBackground(colors) { gradientColors, size ->
    Brush.horizontalGradient(
        colors = gradientColors,
        startX = 0f,
        endX = size.width
    )
}

fun Modifier.gradientBackground(
    colors: List<Color>,
    brushProvider: (List<Color>, Size) -> Brush
): Modifier = composed {
    var size by remember { mutableStateOf(Size.Zero) }
    val gradient = remember(colors, size) { brushProvider(colors, size) }
    drawWithContent {
        size = this.size
        drawRect(brush = gradient)
        drawContent()
    }
}

@Composable
fun ScrollableContent(scrollState: ScrollState, surfaceGradient: List<Color>,
                      playbackConnection :PlaybackConnection,
                      radioList: List<Radio>) {
    Column(
        modifier = Modifier
            .horizontalGradientBackground(surfaceGradient)
            .padding(8.dp)
            .verticalScroll(state = scrollState)
    ) {
        Spacer(modifier = Modifier.height(0.dp))
        SpotifyTitle(
            stringResource(R.string.radio_title))
        HomeGridSection(playbackConnection = playbackConnection, radioList = radioList)
        HomeLanesSection()
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SpotifyTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = typography.h5.copy(fontWeight = FontWeight.ExtraBold),
        modifier = modifier
            .padding(start = 8.dp, end = 4.dp, bottom = 8.dp, top = 24.dp)
            .semantics { heading() }
    )
}

@Composable
fun HomeGridSection(playbackConnection : PlaybackConnection, radioList : List<Radio>) {
    val items = remember { radioList }
    VerticalGrid {
        items.forEach {

            SpotifyHomeGridItem(radio = it, playbackConnection = playbackConnection)
        }
    }
}



@Composable
fun HomeLanesSection() {
    val categories = remember { AlbumsDataProvider.listOfSpotifyHomeLanes }
    categories.forEachIndexed { index, lane ->
        SpotifyTitle(text = lane)
        SpotifyLane(index)
    }
}

@Composable
fun SpotifyLane(index: Int) {
    val itemsEven = remember { AlbumsDataProvider.albums }
    val itemsOdd = remember { AlbumsDataProvider.albums.asReversed() }
    LazyRow {
        items(if (index % 2 == 0) itemsEven else itemsOdd) {
            SpotifyLaneItem(album = it)
        }
    }
}

@Preview
@Composable
fun PreviewSpotifyHome() {
    PreviewSpotifyHome()
}
