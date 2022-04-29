/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.library.playlists.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.alashow.base.util.asString
import com.alashow.base.util.localizedTitle
import com.alashow.base.util.toUiMessage
import com.alashow.datmusic.data.observers.playlist.NoResultsForPlaylistFilter
import com.alashow.datmusic.domain.entities.PlaylistItems
import com.alashow.datmusic.ui.detail.MediaDetailFail
import com.alashow.domain.models.Async
import com.alashow.domain.models.Fail
import com.alashow.ui.components.ErrorBox
import com.alashow.ui.theme.AppTheme

class PlaylistDetailFail : MediaDetailFail<PlaylistItems>() {

    override operator fun invoke(
        list: LazyListScope,
        details: Async<PlaylistItems>,
        onFailRetry: () -> Unit,
    ) {
        if (details is Fail) {
            list.item {
                val error = details.error
                val errorMessage = details.error.toUiMessage().asString(LocalContext.current)
                when (error) {
                    is NoResultsForPlaylistFilter -> Text(
                        text = errorMessage,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.specs.padding)
                    )
                    else -> ErrorBox(
                        title = stringResource(details.error.localizedTitle()),
                        message = errorMessage,
                        onRetryClick = onFailRetry,
                        modifier = Modifier.fillParentMaxHeight(0.5f)
                    )
                }
            }
        }
    }
}
