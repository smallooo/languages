/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.observers.playlist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.alashow.data.SubjectInteractor
import com.alashow.datmusic.data.repos.playlist.PlaylistsRepo
import com.alashow.datmusic.domain.entities.Playlists
import com.alashow.domain.models.Params

class ObservePlaylists @Inject constructor(
    private val playlistsRepo: PlaylistsRepo
) : SubjectInteractor<Params, Playlists>() {
    override fun createObservable(params: Params): Flow<Playlists> = playlistsRepo.playlists()
}
