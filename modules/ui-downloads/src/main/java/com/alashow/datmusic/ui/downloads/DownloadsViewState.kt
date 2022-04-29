/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.downloads

import com.alashow.datmusic.downloader.DownloadItems
import com.alashow.datmusic.downloader.observers.ObserveDownloads
import com.alashow.domain.models.Async
import com.alashow.domain.models.Uninitialized

data class DownloadsViewState(
    val downloads: Async<DownloadItems> = Uninitialized,
    val params: ObserveDownloads.Params = ObserveDownloads.Params()
) {

    companion object {
        val Empty = DownloadsViewState()
    }
}
