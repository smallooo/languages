/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.interactors.search

import javax.inject.Inject
import kotlinx.coroutines.withContext
import com.alashow.base.util.CoroutineDispatchers
import com.alashow.data.Interactor
import com.alashow.data.fetch
import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.data.repos.search.DatmusicSearchStore
import com.alashow.domain.models.BaseEntity

class SearchDatmusic<T : BaseEntity> @Inject constructor(
    private val datmusicSearchStore: DatmusicSearchStore<T>,
    private val dispatchers: CoroutineDispatchers
) : Interactor<SearchDatmusic.Params>() {

    data class Params(val searchParams: DatmusicSearchParams, val forceRefresh: Boolean = false)

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            datmusicSearchStore.fetch(params.searchParams, params.forceRefresh)
        }
    }
}
