/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.interactors.playlist

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.alashow.base.util.CoroutineDispatchers
import com.alashow.data.ResultInteractor
import com.alashow.datmusic.data.repos.playlist.PlaylistsRepo
import com.alashow.datmusic.domain.entities.PlaylistId

class DeletePlaylist @Inject constructor(
    private val repo: PlaylistsRepo,
    private val dispatchers: CoroutineDispatchers,
) : ResultInteractor<PlaylistId, Boolean>() {

    override suspend fun doWork(params: PlaylistId) = withContext(dispatchers.io) {
        return@withContext repo.delete(params) > 0
    }
}
