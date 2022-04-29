/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.interactors.artist

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.alashow.base.util.CoroutineDispatchers
import com.alashow.data.ResultInteractor
import com.alashow.data.fetch
import com.alashow.datmusic.data.DatmusicArtistParams
import com.alashow.datmusic.data.repos.artist.DatmusicArtistDetailsStore
import com.alashow.datmusic.domain.entities.Artist

class GetArtistDetails @Inject constructor(
    private val artistDetailsStore: DatmusicArtistDetailsStore,
    private val dispatchers: CoroutineDispatchers
) : ResultInteractor<GetArtistDetails.Params, Artist>() {

    data class Params(val artistParams: DatmusicArtistParams, val forceRefresh: Boolean = false)

    override suspend fun doWork(params: Params) = withContext(dispatchers.network) {
        artistDetailsStore.fetch(params.artistParams, params.forceRefresh)
    }
}
