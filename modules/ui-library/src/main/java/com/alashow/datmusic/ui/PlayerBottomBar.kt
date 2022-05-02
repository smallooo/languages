package com.alashow.datmusic.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alashow.datmusic.ui.library.R

@Composable
fun PlayerBottomBar(modifier: Modifier) {
    val bottomBarHeight = 57.dp
    val backgroundColor =
        if (isSystemInDarkTheme()) Color(0xFF2A2A2A) else MaterialTheme.colors.background
    Row(
        modifier = modifier
            .padding(bottom = bottomBarHeight)
            .fillMaxWidth()
            .background(color = backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.adele21),
            modifier = Modifier.size(65.dp),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Text(
            text = "Someone Like you by Adele",
            style = typography.h6.copy(fontSize = 14.sp),
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
        )
        Icon(
            imageVector = Icons.Default.FavoriteBorder, modifier = Modifier.padding(8.dp),
            contentDescription = null
        )
        Icon(
            imageVector = Icons.Default.PlayArrow, modifier = Modifier.padding(8.dp),
            contentDescription = null
        )
    }
}
