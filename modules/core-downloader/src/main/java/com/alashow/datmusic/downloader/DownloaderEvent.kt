/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.downloader

import com.alashow.datmusic.downloader.DownloaderEvent.ChooseDownloadsLocation.message
import com.alashow.i18n.UiMessage
import com.alashow.i18n.UiMessageConvertable

typealias DownloaderEvents = List<DownloaderEvent>

data class DownloaderEventsError(val events: DownloaderEvents) : Throwable(), UiMessageConvertable {
    override fun toUiMessage() = events.first().toUiMessage()
}

sealed class DownloaderEvent : UiMessageConvertable {
    object ChooseDownloadsLocation : DownloaderEvent() {
        val message = UiMessage.Resource(R.string.downloader_enqueue_downloadsLocationNotSelected)
    }

    data class DownloaderMessage(val message: UiMessage<*>) : DownloaderEvent()
    data class DownloaderFetchError(val error: Throwable) : DownloaderEvent()

    override fun toUiMessage() = when (this) {
        is ChooseDownloadsLocation -> message
        is DownloaderMessage -> message
        is DownloaderFetchError -> UiMessage.Error(this.error)
    }
}
