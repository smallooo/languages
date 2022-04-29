/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.album

import android.content.Context
import com.alashow.datmusic.domain.entities.Album
import com.alashow.datmusic.domain.entities.Audios
import com.alashow.datmusic.ui.detail.MediaDetailViewState
import com.alashow.domain.models.Async
import com.alashow.domain.models.Success
import com.alashow.domain.models.Uninitialized

data class AlbumDetailViewState(
    val album: Album? = null,
    val albumDetails: Async<Audios> = Uninitialized
) : MediaDetailViewState<Audios> {

    override val isLoaded = album != null
    override val isEmpty = albumDetails is Success && albumDetails.invoke().isEmpty()
    override val title = album?.title

    override fun artwork(context: Context) = album?.photo?.mediumUrl
    override fun details() = albumDetails

    companion object {
        val Empty = AlbumDetailViewState()
    }
}
