/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.repos.album

import javax.inject.Inject
import com.alashow.base.util.CoroutineDispatchers
import com.alashow.data.resultApiCall
import com.alashow.datmusic.data.DatmusicAlbumParams
import com.alashow.datmusic.data.DatmusicAlbumParams.Companion.toQueryMap
import com.alashow.datmusic.data.api.DatmusicEndpoints
import com.alashow.datmusic.domain.models.ApiResponse
import com.alashow.datmusic.domain.models.checkForErrors

class DatmusicAlbumDataSource @Inject constructor(
    private val endpoints: DatmusicEndpoints,
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(params: DatmusicAlbumParams): Result<ApiResponse> {
        return resultApiCall(dispatchers.network) {
            endpoints.album(params.id, params.toQueryMap())
                .checkForErrors()
        }
    }
}
