/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.library.playlists.detail

import android.content.Context
import com.alashow.datmusic.data.observers.playlist.ObservePlaylistDetails
import com.alashow.datmusic.domain.entities.Playlist
import com.alashow.datmusic.domain.entities.PlaylistItems
import com.alashow.datmusic.ui.detail.MediaDetailViewState
import com.alashow.datmusic.ui.utils.AudiosCountDuration
import com.alashow.domain.models.Async
import com.alashow.domain.models.Success
import com.alashow.domain.models.Uninitialized

data class PlaylistDetailViewState(
    val playlist: Playlist? = null,
    val playlistDetails: Async<PlaylistItems> = Uninitialized,
    val params: ObservePlaylistDetails.Params = ObservePlaylistDetails.Params(),
    val audiosCountDuration: AudiosCountDuration? = null,
) : MediaDetailViewState<PlaylistItems> {

    override val isLoaded = playlist != null
    override val isEmpty = playlistDetails is Success && playlistDetails.invoke().isEmpty()
    override val title = playlist?.name

    override fun artwork(context: Context) = playlist?.artworkFile()
    override fun details() = playlistDetails

    companion object {
        val Empty = PlaylistDetailViewState()
    }
}
