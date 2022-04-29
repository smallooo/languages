/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.observers.playlist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.alashow.data.SubjectInteractor
import com.alashow.datmusic.data.repos.playlist.PlaylistsRepo
import com.alashow.datmusic.domain.entities.Playlist
import com.alashow.datmusic.domain.entities.PlaylistId

class ObservePlaylist @Inject constructor(
    private val playlistsRepo: PlaylistsRepo
) : SubjectInteractor<PlaylistId, Playlist>() {
    override fun createObservable(params: PlaylistId): Flow<Playlist> = playlistsRepo.playlist(params)
}
