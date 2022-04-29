/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.audios

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import timber.log.Timber
import com.alashow.base.util.event
import com.alashow.base.util.extensions.simpleName
import com.alashow.base.util.toast
import com.alashow.common.compose.LocalAnalytics
import com.alashow.common.compose.LocalPlaybackConnection
import com.alashow.datmusic.downloader.Downloader
import com.alashow.datmusic.playback.PlaybackConnection
import com.alashow.datmusic.ui.downloader.LocalDownloader
import com.alashow.datmusic.ui.media.R

val LocalAudioActionHandler = staticCompositionLocalOf<AudioActionHandler> {
    error("No LocalAudioActionHandler provided")
}

typealias AudioActionHandler = (AudioItemAction) -> Unit

@Composable
fun audioActionHandler(
    downloader: Downloader = LocalDownloader.current,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
    clipboardManager: ClipboardManager = LocalClipboardManager.current,
    analytics: FirebaseAnalytics = LocalAnalytics.current,
): AudioActionHandler {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    return { action ->
        analytics.event("audio.${action.simpleName}", mapOf("id" to action.audio.id))
        when (action) {
            is AudioItemAction.Play -> playbackConnection.playAudio(action.audio)
            is AudioItemAction.PlayNext -> playbackConnection.playNextAudio(action.audio)
            is AudioItemAction.Download -> coroutine.launch {
                Timber.d("Coroutine launched to download audio: $action")
                downloader.enqueueAudio(action.audio)
            }
            is AudioItemAction.DownloadById -> coroutine.launch {
                Timber.d("Coroutine launched to download audio by Id: $action")
                downloader.enqueueAudio(action.audio.id)
            }
            is AudioItemAction.CopyLink -> {
                clipboardManager.setText(AnnotatedString(action.audio.downloadUrl ?: ""))
                context.toast(R.string.generic_clipboard_copied)
            }
            else -> Timber.e("Unhandled audio action: $action")
        }
    }
}
