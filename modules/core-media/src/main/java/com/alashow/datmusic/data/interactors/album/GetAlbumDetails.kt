/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.interactors.album

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.alashow.base.util.CoroutineDispatchers
import com.alashow.data.ResultInteractor
import com.alashow.data.fetch
import com.alashow.datmusic.data.DatmusicAlbumParams
import com.alashow.datmusic.data.repos.album.DatmusicAlbumDetailsStore
import com.alashow.datmusic.domain.entities.Audio

class GetAlbumDetails @Inject constructor(
    private val albumDetailsStore: DatmusicAlbumDetailsStore,
    private val dispatchers: CoroutineDispatchers
) : ResultInteractor<GetAlbumDetails.Params, List<Audio>>() {

    data class Params(val albumParams: DatmusicAlbumParams, val forceRefresh: Boolean = false)

    override suspend fun doWork(params: Params) = withContext(dispatchers.io) {
        albumDetailsStore.fetch(params.albumParams, params.forceRefresh)
    }
}
