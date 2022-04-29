/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.library.playlists.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Modifier
import com.alashow.datmusic.domain.entities.*
import com.alashow.datmusic.ui.audios.AudioRow
import com.alashow.datmusic.ui.detail.MediaDetailContent
import com.alashow.datmusic.ui.library.R
import com.alashow.domain.models.Async
import com.alashow.domain.models.Loading
import com.alashow.domain.models.Success

private val RemoveFromPlaylist = R.string.playlist_audio_removeFromPlaylist

class PlaylistDetailContent(
    private val onPlayAudio: (PlaylistItem) -> Unit,
    private val onRemoveFromPlaylist: (PlaylistItem) -> Unit,
) : MediaDetailContent<PlaylistItems>() {

    @OptIn(ExperimentalFoundationApi::class)
    override fun invoke(list: LazyListScope, details: Async<PlaylistItems>, detailsLoading: Boolean): Boolean {
        val playlistAudios = when (details) {
            is Success -> details()
            is Loading -> (1..10).map { PlaylistItem(PlaylistAudio(it.toLong())) }
            else -> emptyList()
        }

        if (playlistAudios.isNotEmpty()) {
            list.itemsIndexed(playlistAudios, key = { _, it -> it.playlistAudio.id }) { index, item ->
                AudioRow(
                    audio = item.audio,
                    audioIndex = index,
                    isPlaceholder = detailsLoading,
                    onPlayAudio = {
                        if (details is Success)
                            onPlayAudio(item)
                    },
                    extraActionLabels = listOf(RemoveFromPlaylist),
                    onExtraAction = { onRemoveFromPlaylist(item) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
        return playlistAudios.isEmpty()
    }
}
