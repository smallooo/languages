/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.alashow.base.ui.SnackbarManager
import com.alashow.base.ui.SnackbarMessage
import com.alashow.base.util.event
import com.alashow.base.util.extensions.stateInDefault
import com.alashow.base.util.toUiMessage
import com.alashow.datmusic.data.interactors.playlist.DeletePlaylist
import com.alashow.datmusic.data.interactors.playlist.DownloadPlaylist
import com.alashow.datmusic.data.observers.playlist.ObservePlaylists
import com.alashow.datmusic.domain.entities.PlaylistId
import com.alashow.domain.models.Fail
import com.alashow.domain.models.Loading
import com.alashow.domain.models.Params
import com.alashow.domain.models.Success
import com.alashow.domain.models.Uninitialized
import com.alashow.i18n.UiMessage
import com.alashow.navigation.Navigator
import com.alashow.navigation.screens.LeafScreen

object PlaylistDownloadQueued : SnackbarMessage<Unit>(UiMessage.Resource(R.string.playlist_download_queued))
data class PlaylistDownloadQueuedCount(val count: Int) :
    SnackbarMessage<Unit>(UiMessage.Resource(R.string.playlist_download_queuedCount, listOf(count)))

@HiltViewModel
class LibraryViewModel @Inject constructor(
    handle: SavedStateHandle,
    observePlaylists: ObservePlaylists,
    private val playlistDeleter: DeletePlaylist,
    private val playlistDownloader: DownloadPlaylist,
    private val snackbarManager: SnackbarManager,
    private val analytics: FirebaseAnalytics,
    private val navigator: Navigator,
) : ViewModel() {

    val libraryItems = observePlaylists.flow
        .map { Success(it) }
        .stateInDefault(viewModelScope, Uninitialized)

    init {
        observePlaylists(Params())
    }

    fun deletePlaylist(playlistId: PlaylistId) = viewModelScope.launch {
        analytics.event("playlist.row.delete", mapOf("playlistId" to playlistId))
        playlistDeleter.execute(playlistId)
    }

    fun downloadPlaylist(playlistId: PlaylistId) = viewModelScope.launch {
        analytics.event("playlist.row.download", mapOf("playlistId" to playlistId))
        playlistDownloader(playlistId).collectLatest { result ->
            when (result) {
                is Fail -> snackbarManager.addMessage(result.error.toUiMessage())
                is Loading -> {
                    snackbarManager.addMessage(PlaylistDownloadQueued)
                    navigator.navigate(LeafScreen.Downloads().createRoute())
                }
                is Success -> {
                    val queuedCount = result()
                    if (queuedCount > 1)
                        snackbarManager.addMessage(PlaylistDownloadQueuedCount(count = queuedCount))
                }
                else -> Unit
            }
        }
    }
}
