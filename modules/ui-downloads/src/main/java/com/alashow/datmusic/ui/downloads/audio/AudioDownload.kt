/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.downloads.audio

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status
import com.alashow.base.util.extensions.interpunctize
import com.alashow.datmusic.domain.entities.AudioDownloadItem
import com.alashow.datmusic.downloader.Downloader
import com.alashow.datmusic.downloader.isComplete
import com.alashow.datmusic.downloader.isPausable
import com.alashow.datmusic.downloader.isResumable
import com.alashow.datmusic.downloader.isRetriable
import com.alashow.datmusic.downloader.progressVisible
import com.alashow.datmusic.ui.audios.AudioRowItem
import com.alashow.datmusic.ui.downloads.R
import com.alashow.datmusic.ui.downloads.fileSizeStatus
import com.alashow.datmusic.ui.downloads.statusLabel
import com.alashow.datmusic.ui.library.playlist.addTo.AddToPlaylistMenu
import com.alashow.ui.TimedVisibility
import com.alashow.ui.colorFilterDynamicProperty
import com.alashow.ui.components.IconButton
import com.alashow.ui.components.ProgressIndicator
import com.alashow.ui.theme.AppTheme

@Composable
internal fun AudioDownload(
    audioDownloadItem: AudioDownloadItem,
    modifier: Modifier = Modifier,
    onAudioPlay: (AudioDownloadItem) -> Unit,
    actionHandler: AudioDownloadItemActionHandler = LocalAudioDownloadItemActionHandler.current
) {
    val audio = audioDownloadItem.audio
    val downloadInfo = audioDownloadItem.downloadInfo
    var menuVisible by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.specs.padding),
        modifier = modifier
            .clickable {
                if (downloadInfo.isComplete()) onAudioPlay(audioDownloadItem)
                else menuVisible = true
            }
            .fillMaxWidth()
            .padding(AppTheme.specs.inputPaddings)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AudioRowItem(
                audio = audioDownloadItem.audio,
                modifier = Modifier.weight(16f),
                onCoverClick = { onAudioPlay(audioDownloadItem) }
            )
            DownloadRequestProgress(
                downloadInfo = downloadInfo,
                onClick = {
                    when {
                        downloadInfo.isRetriable() -> AudioDownloadItemAction.Retry(audioDownloadItem)
                        downloadInfo.isResumable() -> AudioDownloadItemAction.Resume(audioDownloadItem)
                        downloadInfo.isPausable() -> AudioDownloadItemAction.Pause(audioDownloadItem)
                        else -> null
                    }?.run(actionHandler)
                },
                progress = downloadInfo.progress / 100f,
                modifier = Modifier.weight(4f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                val fileSize = downloadInfo.fileSizeStatus()
                val status = downloadInfo.statusLabel().let {
                    when (fileSize.isNotBlank()) {
                        true -> it.lowercase()
                        else -> it
                    }
                }

                val footer = listOf(fileSize, status).filter { it.isNotBlank() }.interpunctize()
                Text(text = footer, modifier = Modifier.weight(19f))
            }

            val (addToPlaylistVisible, setAddToPlaylistVisible) = remember { mutableStateOf(false) }
            AddToPlaylistMenu(audio, addToPlaylistVisible, setAddToPlaylistVisible)
            AudioDownloadDropdownMenu(
                audioDownload = audioDownloadItem.copy(downloadInfo = downloadInfo),
                expanded = menuVisible,
                onExpandedChange = { menuVisible = it },
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp)
            ) { actionLabel ->
                when (val action = AudioDownloadItemAction.from(actionLabel, audioDownloadItem)) {
                    is AudioDownloadItemAction.Play -> onAudioPlay(action.audio)
                    is AudioDownloadItemAction.AddToPlaylist -> setAddToPlaylistVisible(true)
                    else -> actionHandler(action)
                }
            }
        }
    }
}

@Composable
private fun DownloadRequestProgress(
    downloadInfo: Download,
    onClick: () -> Unit,
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = AppTheme.specs.iconSize,
    strokeWidth: Dp = 1.dp,
) {
    val paused = downloadInfo.isResumable()
    val queued = downloadInfo.status == Status.QUEUED
    val retriable = downloadInfo.isRetriable()
    val previousStatus by remember { mutableStateOf(downloadInfo.status) }

    val justCompleted by remember(downloadInfo) {
        derivedStateOf {
            (previousStatus == Status.DOWNLOADING || previousStatus == Status.QUEUED) && downloadInfo.status == Status.COMPLETED
        }
    }

    val progressAnimated by animateFloatAsState(
        progress.coerceIn(0f, 1f),
        animationSpec = tween((Downloader.DOWNLOADS_STATUS_REFRESH_INTERVAL * 1.3).toInt(), easing = LinearEasing)
    )

    if (downloadInfo.progressVisible() || justCompleted)
        Box(
            modifier = modifier
                .width(size)
                .clip(CircleShape),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (queued) {
                ProgressIndicator(
                    size = size,
                    strokeWidth = strokeWidth,
                    modifier = Modifier
                        .size(size)
                        .padding(AppTheme.specs.paddingTiny)
                )
            } else if (downloadInfo.progressVisible()) {
                CircularProgressIndicator(
                    progress = progressAnimated,
                    color = MaterialTheme.colors.secondary,
                    strokeWidth = strokeWidth,
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape),
                )
                IconButton(
                    onClick = onClick,
                    rippleColor = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(size)
                        .background(MaterialTheme.colors.secondary.copy(alpha = 0.1f))
                ) {
                    val icon = when {
                        retriable -> Icons.Filled.Refresh
                        paused -> Icons.Filled.PlayArrow
                        else -> Icons.Filled.Pause
                    }
                    Crossfade(icon) {
                        Icon(
                            painter = rememberVectorPainter(it),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(AppTheme.specs.paddingSmall)
                        )
                    }
                }
            }
            if (justCompleted) {
                TimedVisibility {
                    val completeComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.complete))
                    LottieAnimation(
                        completeComposition,
                        modifier = Modifier.size(size + AppTheme.specs.paddingTiny),
                        dynamicProperties = colorFilterDynamicProperty()
                    )
                }
            }
        }
}
