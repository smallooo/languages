/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.interactors.playlist

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.alashow.base.util.CoroutineDispatchers
import com.alashow.data.Interactor
import com.alashow.data.ResultInteractor
import com.alashow.datmusic.data.repos.playlist.PlaylistsRepo
import com.alashow.datmusic.domain.entities.Playlist
import com.alashow.datmusic.domain.entities.PlaylistAudioIds
import com.alashow.datmusic.domain.entities.PlaylistId
import com.alashow.datmusic.domain.entities.PlaylistItems

class UpdatePlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<Playlist, Playlist>() {

    override suspend fun doWork(params: Playlist) = withContext(dispatchers.io) {
        return@withContext repo.updatePlaylist(params)
    }
}

class ReorderPlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : Interactor<ReorderPlaylist.Params>() {

    data class Params(val playlistId: PlaylistId, val from: Int, val to: Int)

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            repo.swapPositions(params.playlistId, params.from, params.to)
        }
    }
}

class UpdatePlaylistItems @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : Interactor<PlaylistItems>() {

    override suspend fun doWork(params: PlaylistItems) {
        withContext(dispatchers.io) {
            repo.updatePlaylistItems(params)
        }
    }
}

class RemovePlaylistItems @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<PlaylistAudioIds, Int>() {

    override suspend fun doWork(params: PlaylistAudioIds) = withContext(dispatchers.io) {
        return@withContext repo.removePlaylistItems(params)
    }
}
