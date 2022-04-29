/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.interactors.playlist

import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.alashow.base.billing.Subscriptions
import com.alashow.base.util.CoroutineDispatchers
import com.alashow.data.AsyncInteractor
import com.alashow.datmusic.coreLibrary.R
import com.alashow.datmusic.data.repos.playlist.PlaylistsRepo
import com.alashow.datmusic.domain.entities.PlaylistId
import com.alashow.datmusic.downloader.Downloader
import com.alashow.datmusic.downloader.DownloaderEventsError
import com.alashow.i18n.UiMessage
import com.alashow.i18n.ValidationError

object PlaylistIsEmpty : ValidationError(UiMessage.Resource(R.string.playlist_download_error_empty))

class DownloadPlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val downloader: Downloader,
    private val dispatchers: CoroutineDispatchers,
) : AsyncInteractor<PlaylistId, Int>() {

    override suspend fun prepare(params: PlaylistId) {
        downloader.clearDownloaderEvents()
        Subscriptions.checkPremiumPermission()
        if (repo.playlistItems(params).first().isEmpty())
            throw PlaylistIsEmpty.error()
    }

    override suspend fun doWork(params: PlaylistId) = withContext(dispatchers.io) {
        val audios = repo.playlistItems(params).first().map { it.audio }
        var enqueuedCount = 0

        audios.forEach {
            if (downloader.enqueueAudio(it))
                enqueuedCount++
        }

        val events = downloader.getDownloaderEvents()
        if (enqueuedCount == 0 && events.isNotEmpty())
            throw DownloaderEventsError(events)

        return@withContext enqueuedCount
    }
}
