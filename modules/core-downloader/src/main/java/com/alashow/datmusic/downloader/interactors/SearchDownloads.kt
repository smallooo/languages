/*
 * Copyright (C) 2022, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.downloader.interactors

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.alashow.data.SubjectInteractor
import com.alashow.datmusic.data.db.daos.AudiosFtsDao
import com.alashow.datmusic.domain.entities.DownloadRequest

class SearchDownloads @Inject constructor(
    private val audiosFtsDao: AudiosFtsDao,
) : SubjectInteractor<String, List<DownloadRequest>>() {
    override fun createObservable(params: String): Flow<List<DownloadRequest>> {
        return audiosFtsDao.searchDownloads("*$params*")
    }
}
