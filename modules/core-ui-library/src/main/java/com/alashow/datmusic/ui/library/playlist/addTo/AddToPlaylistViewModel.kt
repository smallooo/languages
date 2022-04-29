/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.library.playlist.addTo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import com.alashow.base.ui.SnackbarAction
import com.alashow.base.ui.SnackbarManager
import com.alashow.base.ui.SnackbarMessage
import com.alashow.base.util.event
import com.alashow.base.util.extensions.stateInDefault
import com.alashow.datmusic.data.interactors.playlist.AddToPlaylist
import com.alashow.datmusic.data.interactors.playlist.CreatePlaylist
import com.alashow.datmusic.data.observers.playlist.ObservePlaylists
import com.alashow.datmusic.domain.entities.AudioIds
import com.alashow.datmusic.domain.entities.Playlist
import com.alashow.datmusic.domain.entities.PlaylistId
import com.alashow.datmusic.ui.coreLibrary.R
import com.alashow.datmusic.ui.library.playlist.addTo.NewPlaylistItem.isNewPlaylistItem
import com.alashow.domain.models.Params
import com.alashow.i18n.UiMessage
import com.alashow.navigation.Navigator
import com.alashow.navigation.screens.LeafScreen

data class AddedToPlaylistMessage(val playlist: Playlist) :
    SnackbarMessage<PlaylistId>(
        message = UiMessage.Resource(R.string.playlist_addTo_added, formatArgs = listOf(playlist.name)),
        action = SnackbarAction(UiMessage.Resource(R.string.playlist_addTo_open), playlist.id)
    )

@HiltViewModel
class AddToPlaylistViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val observePlaylists: ObservePlaylists,
    private val addToPlaylist: AddToPlaylist,
    private val createPlaylist: CreatePlaylist,
    private val snackbarManager: SnackbarManager,
    private val analytics: FirebaseAnalytics,
    private val navigator: Navigator,
) : ViewModel() {

    val playlists = observePlaylists.flow.stateInDefault(viewModelScope, emptyList())

    init {
        observePlaylists(Params())
    }

    fun addTo(playlist: Playlist, audioIds: AudioIds) {
        analytics.event("playlists.addTo", mapOf("playlistId" to playlist.id, "audiosIds" to audioIds.joinToString()))
        viewModelScope.launch {
            var targetPlaylist = playlist
            if (playlist.isNewPlaylistItem()) {
                targetPlaylist = createPlaylist.execute(CreatePlaylist.Params(generateNameIfEmpty = true))
            }

            addToPlaylist.execute(AddToPlaylist.Params(targetPlaylist.id, audioIds))

            val addedToPlaylist = AddedToPlaylistMessage(targetPlaylist)
            snackbarManager.addMessage(addedToPlaylist)
            if (snackbarManager.observeMessageAction(addedToPlaylist) != null)
                navigator.navigate(LeafScreen.PlaylistDetail.buildRoute(targetPlaylist.id))
        }
    }
}
