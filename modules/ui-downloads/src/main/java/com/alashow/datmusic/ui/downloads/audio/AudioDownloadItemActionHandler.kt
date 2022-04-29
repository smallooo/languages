/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.downloads.audio

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
import com.alashow.base.util.IntentUtils
import com.alashow.base.util.event
import com.alashow.base.util.extensions.simpleName
import com.alashow.base.util.toast
import com.alashow.common.compose.LocalAnalytics
import com.alashow.common.compose.LocalPlaybackConnection
import com.alashow.datmusic.downloader.Downloader
import com.alashow.datmusic.playback.PlaybackConnection
import com.alashow.datmusic.ui.downloader.LocalDownloader
import com.alashow.datmusic.ui.media.R

val LocalAudioDownloadItemActionHandler = staticCompositionLocalOf<AudioDownloadItemActionHandler> {
    error("No AudioDownloadItemActionHandler provided")
}

typealias AudioDownloadItemActionHandler = (AudioDownloadItemAction) -> Unit

@Composable
fun audioDownloadItemActionHandler(
    downloader: Downloader = LocalDownloader.current,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
    clipboardManager: ClipboardManager = LocalClipboardManager.current,
    analytics: FirebaseAnalytics = LocalAnalytics.current
): AudioDownloadItemActionHandler {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    return { action ->
        analytics.event("downloads.audio.${action.simpleName}", mapOf("id" to action.audio.audio.id))
        coroutine.launch {
            when (action) {
                is AudioDownloadItemAction.Play -> playbackConnection.playAudio(action.audio.audio)
                is AudioDownloadItemAction.PlayNext -> playbackConnection.playNextAudio(action.audio.audio)
                is AudioDownloadItemAction.Pause -> downloader.pause(action.audio)
                is AudioDownloadItemAction.Resume -> downloader.resume(action.audio)
                is AudioDownloadItemAction.Cancel -> downloader.cancel(action.audio)
                is AudioDownloadItemAction.Retry -> downloader.retry(action.audio)
                is AudioDownloadItemAction.Remove -> downloader.remove(action.audio)
                is AudioDownloadItemAction.Delete -> downloader.delete(action.audio)
                is AudioDownloadItemAction.Open -> IntentUtils.openFile(context, action.audio.downloadInfo.fileUri, action.audio.audio.fileMimeType())
                is AudioDownloadItemAction.CopyLink -> {
                    clipboardManager.setText(AnnotatedString(action.audio.audio.downloadUrl ?: ""))
                    context.toast(R.string.generic_clipboard_copied)
                }
                else -> Timber.e("Unhandled action: $action")
            }
        }
    }
}
