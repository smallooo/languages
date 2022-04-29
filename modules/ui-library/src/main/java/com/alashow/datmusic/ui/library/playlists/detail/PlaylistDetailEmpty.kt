/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.library.playlists.detail

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alashow.datmusic.domain.entities.PlaylistItems
import com.alashow.datmusic.ui.detail.MediaDetailEmpty
import com.alashow.datmusic.ui.library.R
import com.alashow.domain.models.Async
import com.alashow.domain.models.Success
import com.alashow.ui.Delayed
import com.alashow.ui.components.EmptyErrorBox

class PlaylistDetailEmpty : MediaDetailEmpty<PlaylistItems>() {
    override operator fun invoke(
        list: LazyListScope,
        details: Async<PlaylistItems>,
        isHeaderVisible: Boolean,
        detailsEmpty: Boolean,
        onEmptyRetry: () -> Unit
    ) {
        if (details is Success && detailsEmpty) {
            list.item {
                Delayed {
                    EmptyErrorBox(
                        onRetryClick = onEmptyRetry,
                        message = stringResource(R.string.playlist_empty),
                        retryLabel = stringResource(R.string.playlist_empty_addSongs),
                        modifier = Modifier.fillParentMaxHeight(if (isHeaderVisible) 0.5f else 1f)
                    )
                }
            }
        }
    }
}
