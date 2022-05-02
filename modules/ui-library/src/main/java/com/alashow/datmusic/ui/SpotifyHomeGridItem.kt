package com.alashow.datmusic.ui

import androidx.annotation.FloatRange
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.alashow.common.compose.LocalPlaybackConnection
import com.alashow.datmusic.data.Album
import com.alashow.datmusic.data.AlbumsDataProvider
import com.alashow.datmusic.data.Radio
import com.alashow.datmusic.data.RadioList
import com.alashow.datmusic.playback.PlaybackConnection
import com.alashow.datmusic.playback.playPause
import com.alashow.datmusic.ui.audios.AudioItemAction
import com.alashow.datmusic.ui.audios.currentPlayingMenuActionLabels
import com.alashow.datmusic.ui.library.R
import com.alashow.ui.components.CoverImage
import com.alashow.ui.components.shimmer
import com.google.accompanist.placeholder.PlaceholderDefaults
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.shimmer
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.Serializable
import java.net.URL




@Composable
fun SpotifyHomeGridItem(radio: Radio, playbackConnection : PlaybackConnection) {
    val cardColor = if (isSystemInDarkTheme()) Color(0xFF2A2A2A) else MaterialTheme.colors.background
    Card(
        elevation = 4.dp,
        backgroundColor = cardColor,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable(onClick = {
                //"https://sarock.radioca.st/stream;"
                playbackConnection.playRadio(radio.url)
                //AudioItemAction.Play()
            })
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CoverImage(
                data = radio.favicon,
                size = 55.dp,
                icon = rememberVectorPainter(Icons.Default.Album),
            )

            Text(
                text = radio.name,
                maxLines = 2,
                style = typography.h6.copy(fontSize = 14.sp),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewSpotifyHomeGridItem() {
//    val album = remember { AlbumsDataProvider.album }
//    val playbackConnection = LocalPlaybackConnection.current
//    SpotifyHomeGridItem(album, playbackConnection)
}
