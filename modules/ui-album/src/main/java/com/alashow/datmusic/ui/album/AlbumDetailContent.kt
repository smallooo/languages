/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.album

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import com.alashow.common.compose.LocalPlaybackConnection
import com.alashow.datmusic.domain.entities.Album
import com.alashow.datmusic.domain.entities.Audio
import com.alashow.datmusic.domain.entities.Audios
import com.alashow.datmusic.ui.audios.AudioRow
import com.alashow.datmusic.ui.detail.MediaDetailContent
import com.alashow.domain.models.Async
import com.alashow.domain.models.Loading
import com.alashow.domain.models.Success

class AlbumDetailContent(val album: Album) : MediaDetailContent<Audios>() {

    override fun invoke(list: LazyListScope, details: Async<Audios>, detailsLoading: Boolean): Boolean {
        val albumAudios = when (details) {
            is Success -> details()
            is Loading -> (1..album.songCount).map { Audio() }
            else -> emptyList()
        }

        if (albumAudios.isNotEmpty()) {
            list.itemsIndexed(albumAudios, key = { i, a -> a.id + i }) { index, audio ->
                val playbackConnection = LocalPlaybackConnection.current
                AudioRow(
                    audio = audio,
                    isPlaceholder = detailsLoading,
                    includeCover = false,
                    onPlayAudio = {
                        if (details is Success)
                            playbackConnection.playAlbum(album.id, index)
                    }
                )
            }
        }
        return albumAudios.isEmpty()
    }
}
