/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.alashow.base.util.extensions.stateInDefault
import com.alashow.datmusic.data.DatmusicAlbumParams
import com.alashow.datmusic.data.DatmusicArtistParams
import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.data.interactors.album.GetAlbumDetails
import com.alashow.datmusic.data.observers.album.ObserveAlbum
import com.alashow.datmusic.data.observers.album.ObserveAlbumDetails
import com.alashow.datmusic.data.observers.artist.ObserveArtist
import com.alashow.navigation.Navigator
import com.alashow.navigation.screens.ALBUM_ACCESS_KEY
import com.alashow.navigation.screens.ALBUM_ID_KEY
import com.alashow.navigation.screens.ALBUM_OWNER_ID_KEY
import com.alashow.navigation.screens.LeafScreen

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val albumObserver: ObserveAlbum,
    private val albumDetails: ObserveAlbumDetails,
    private val observeArtist: ObserveArtist,
    private val navigator: Navigator,
) : ViewModel() {

    private val albumParams = DatmusicAlbumParams(
        requireNotNull(handle.get<String>(ALBUM_ID_KEY)),
        requireNotNull(handle.get<Long>(ALBUM_OWNER_ID_KEY)),
        requireNotNull(handle.get<String>(ALBUM_ACCESS_KEY))
    )

    val state = combine(albumObserver.flow, albumDetails.asyncFlow, ::AlbumDetailViewState)
        .stateInDefault(viewModelScope, AlbumDetailViewState.Empty)

    init {
        load()
    }

    private fun load(forceRefresh: Boolean = false) {
        albumObserver(albumParams)
        albumDetails(GetAlbumDetails.Params(albumParams, forceRefresh))
    }

    fun refresh() = load(true)

    /**
     * Navigate to artist detail screen if album's main artist is in the database already, matched by id.
     * Otherwise navigate to search screen with artist name as query.
     */
    fun goToArtist() = viewModelScope.launch {
        val album = state.first().album
        if (album != null) {
            val artist = album.artists.firstOrNull()
            if (artist != null) {
                val id = artist.id
                val name = artist.name
                observeArtist(DatmusicArtistParams(id))
                val route = when (observeArtist.getOrNull() != null) {
                    true -> LeafScreen.ArtistDetails.buildRoute(id)
                    else -> LeafScreen.Search.buildRoute(
                        name,
                        DatmusicSearchParams.BackendType.ARTISTS,
                        DatmusicSearchParams.BackendType.ALBUMS
                    )
                }
                navigator.navigate(route)
            }
        }
    }
}
