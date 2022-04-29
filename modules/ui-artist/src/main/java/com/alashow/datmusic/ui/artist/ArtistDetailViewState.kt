/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.artist

import android.content.Context
import com.alashow.datmusic.domain.entities.Artist
import com.alashow.datmusic.ui.detail.MediaDetailViewState
import com.alashow.domain.models.Async
import com.alashow.domain.models.Success
import com.alashow.domain.models.Uninitialized

data class ArtistDetailViewState(
    val artist: Artist? = null,
    val artistDetails: Async<Artist> = Uninitialized
) : MediaDetailViewState<Artist> {

    override val isLoaded = artist != null
    override val isEmpty = artistDetails is Success && artistDetails.invoke().audios.isEmpty()
    override val title = artist?.name

    override fun artwork(context: Context) = artist?.largePhoto()
    override fun details() = artistDetails

    companion object {
        val Empty = ArtistDetailViewState()
    }
}
