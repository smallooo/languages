/*
 * Copyright (C) 2022, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.interactors.playlist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.alashow.data.SubjectInteractor
import com.alashow.datmusic.data.db.daos.AudiosFtsDao
import com.alashow.datmusic.domain.entities.PlaylistId
import com.alashow.datmusic.domain.entities.PlaylistItem

class SearchPlaylistItems @Inject constructor(
    private val audiosFtsDao: AudiosFtsDao,
) : SubjectInteractor<SearchPlaylistItems.Params, List<PlaylistItem>>() {

    data class Params(val playlistId: PlaylistId, val query: String)

    override fun createObservable(params: Params): Flow<List<PlaylistItem>> {
        return audiosFtsDao.searchPlaylist(params.playlistId, "*${params.query}*")
    }
}
