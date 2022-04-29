/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.repos.artist

import javax.inject.Inject
import com.alashow.base.util.CoroutineDispatchers
import com.alashow.data.resultApiCall
import com.alashow.datmusic.data.DatmusicArtistParams
import com.alashow.datmusic.data.DatmusicArtistParams.Companion.toQueryMap
import com.alashow.datmusic.data.api.DatmusicEndpoints
import com.alashow.datmusic.domain.models.ApiResponse
import com.alashow.datmusic.domain.models.checkForErrors

class DatmusicArtistDataSource @Inject constructor(
    private val endpoints: DatmusicEndpoints,
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(params: DatmusicArtistParams): Result<ApiResponse> {
        return resultApiCall(dispatchers.network) {
            endpoints.artist(params.id, params.toQueryMap())
                .checkForErrors()
        }
    }
}
