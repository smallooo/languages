/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.playback

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.alashow.base.ui.SnackbarAction
import com.alashow.base.ui.SnackbarManager
import com.alashow.base.ui.SnackbarMessage
import com.alashow.base.util.event
import com.alashow.base.util.toUiMessage
import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.data.interactors.playlist.CreatePlaylist
import com.alashow.datmusic.domain.entities.Playlist
import com.alashow.datmusic.domain.entities.PlaylistId
import com.alashow.datmusic.playback.PlaybackConnection
import com.alashow.datmusic.playback.models.*
import com.alashow.datmusic.playback.models.QueueTitle.Companion.asQueueTitle
import com.alashow.datmusic.ui.coreLibrary.R
import com.alashow.i18n.UiMessage
import com.alashow.navigation.Navigator
import com.alashow.navigation.screens.LeafScreen

data class SavedAsPlaylistMessage(val playlist: Playlist) :
    SnackbarMessage<PlaylistId>(
        message = UiMessage.Resource(R.string.playback_queue_saveAsPlaylist_saved, formatArgs = listOf(playlist.name)),
        action = SnackbarAction(UiMessage.Resource(R.string.playback_queue_saveAsPlaylist_open), playlist.id)
    )

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val playbackConnection: PlaybackConnection,
    private val createPlaylist: CreatePlaylist,
    private val snackbarManager: SnackbarManager,
    private val navigator: Navigator,
    private val analytics: FirebaseAnalytics
) : ViewModel() {

    fun saveQueueAsPlaylist() = viewModelScope.launch {
        val queue = playbackConnection.playbackQueue.first()
        analytics.event(
            "playbackSheet.saveQueueAsPlaylist",
            mapOf("count" to queue.size, "queue" to queue.title)
        )

        val params = CreatePlaylist.Params(name = queue.title.asQueueTitle().localizeValue(), audios = queue, trimIfTooLong = true)
        createPlaylist(params)
            .catch { snackbarManager.addMessage(it.toUiMessage()) }
            .collectLatest { playlist ->
                val savedAsPlaylist = SavedAsPlaylistMessage(playlist)
                snackbarManager.addMessage(savedAsPlaylist)
                if (snackbarManager.observeMessageAction(savedAsPlaylist) != null)
                    navigator.navigate(LeafScreen.PlaylistDetail.buildRoute(playlist.id))
            }
    }

    fun navigateToQueueSource() = viewModelScope.launch {
        val queue = playbackConnection.playbackQueue.first()
        val (sourceMediaType, sourceMediaValue) = queue.title.asQueueTitle().sourceMediaId
        analytics.event(
            "playbackSheet.navigateToQueueSource",
            mapOf("sourceMediaType" to sourceMediaType, "sourceMediaValue" to sourceMediaValue)
        )

        when (sourceMediaType) {
            MEDIA_TYPE_PLAYLIST -> navigator.navigate(LeafScreen.PlaylistDetail.buildRoute(sourceMediaValue.toLong()))
            MEDIA_TYPE_DOWNLOADS -> navigator.navigate(LeafScreen.Downloads().createRoute())
            MEDIA_TYPE_ARTIST -> navigator.navigate(LeafScreen.ArtistDetails.buildRoute(sourceMediaValue))
            MEDIA_TYPE_ALBUM -> navigator.navigate(LeafScreen.AlbumDetails.buildRoute(sourceMediaValue))
            MEDIA_TYPE_AUDIO_QUERY -> navigator.navigate(LeafScreen.Search.buildRoute(sourceMediaValue))
            MEDIA_TYPE_AUDIO_MINERVA_QUERY -> navigator.navigate(
                LeafScreen.Search.buildRoute(
                    sourceMediaValue,
                    DatmusicSearchParams.BackendType.MINERVA
                )
            )
            MEDIA_TYPE_AUDIO_FLACS_QUERY -> navigator.navigate(
                LeafScreen.Search.buildRoute(
                    sourceMediaValue,
                    DatmusicSearchParams.BackendType.FLACS
                )
            )
            else -> Unit
        }
    }

    fun onTitleClick() = viewModelScope.launch {
        val nowPlaying = playbackConnection.nowPlaying.value
        val query = nowPlaying.toAlbumSearchQuery()
        analytics.event("playbackSheet.onTitleClick", mapOf("query" to query))
        navigator.navigate(LeafScreen.Search.buildRoute(nowPlaying.toAlbumSearchQuery(), DatmusicSearchParams.BackendType.ALBUMS))
    }

    fun onArtistClick() = viewModelScope.launch {
        val nowPlaying = playbackConnection.nowPlaying.value
        val query = nowPlaying.toArtistSearchQuery()
        analytics.event("playbackSheet.onArtistClick", mapOf("query" to query))
        navigator.navigate(
            LeafScreen.Search.buildRoute(
                query,
                DatmusicSearchParams.BackendType.ARTISTS, DatmusicSearchParams.BackendType.ALBUMS
            )
        )
    }
}
