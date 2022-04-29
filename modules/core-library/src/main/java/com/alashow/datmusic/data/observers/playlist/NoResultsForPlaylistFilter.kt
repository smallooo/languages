/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.observers.playlist

import com.alashow.datmusic.downloader.R
import com.alashow.i18n.UiMessage
import com.alashow.i18n.ValidationError
import com.alashow.i18n.ValidationErrorException

data class NoResultsForPlaylistFilter(val params: ObservePlaylistDetails.Params) :
    ValidationErrorException(
        ValidationError(
            when (params.hasQuery) {
                true -> UiMessage.Resource(
                    R.string.playlist_detail_filter_noResults_forQuery,
                    listOf(params.query)
                )
                else -> UiMessage.Resource(R.string.playlist_detail_filter_noResults)
            }
        )
    )
