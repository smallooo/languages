/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.repos.search

import javax.inject.Inject
import com.alashow.base.util.CoroutineDispatchers
import com.alashow.data.resultApiCall
import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.data.DatmusicSearchParams.Companion.toQueryMap
import com.alashow.datmusic.data.api.DatmusicEndpoints
import com.alashow.datmusic.domain.models.ApiResponse
import com.alashow.datmusic.domain.models.checkForErrors

class DatmusicSearchDataSource @Inject constructor(
    private val endpoints: DatmusicEndpoints,
    private val dispatchers: CoroutineDispatchers
) {
    suspend operator fun invoke(params: DatmusicSearchParams): Result<ApiResponse> {
        return resultApiCall(dispatchers.network) {
            endpoints.multisearch(params.toQueryMap(), *params.types.toTypedArray())
                .checkForErrors()
        }
    }
}
