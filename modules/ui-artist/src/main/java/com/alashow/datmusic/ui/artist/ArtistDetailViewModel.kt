/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import com.alashow.base.util.extensions.stateInDefault
import com.alashow.datmusic.data.DatmusicArtistParams
import com.alashow.datmusic.data.interactors.artist.GetArtistDetails
import com.alashow.datmusic.data.observers.artist.ObserveArtist
import com.alashow.datmusic.data.observers.artist.ObserveArtistDetails
import com.alashow.navigation.screens.ARTIST_ID_KEY

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val artist: ObserveArtist,
    private val artistDetails: ObserveArtistDetails
) : ViewModel() {

    private val artistParams = DatmusicArtistParams(handle.get<String>(ARTIST_ID_KEY)!!)

    val state = combine(artist.flow, artistDetails.asyncFlow, ::ArtistDetailViewState)
        .stateInDefault(viewModelScope, ArtistDetailViewState.Empty)

    init {
        load()
    }

    private fun load(forceRefresh: Boolean = false) {
        artist(artistParams)
        artistDetails(GetArtistDetails.Params(artistParams, forceRefresh))
    }

    fun refresh() = load(true)
}
