/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.observers.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.alashow.data.PaginatedEntryRemoteMediator
import com.alashow.data.PagingInteractor
import com.alashow.data.db.PaginatedEntryDao
import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.data.interactors.search.SearchDatmusic
import com.alashow.domain.models.PaginatedEntity

@OptIn(ExperimentalPagingApi::class)
class ObservePagedDatmusicSearch<T : PaginatedEntity> @Inject constructor(
    private val searchDatmusic: SearchDatmusic<T>,
    private val dao: PaginatedEntryDao<DatmusicSearchParams, T>
) : PagingInteractor<ObservePagedDatmusicSearch.Params<T>, T>() {

    override fun createObservable(
        params: Params<T>
    ): Flow<PagingData<T>> {
        return Pager(
            config = params.pagingConfig,
            remoteMediator = PaginatedEntryRemoteMediator { page, refreshing ->
                try {
                    searchDatmusic.execute(
                        SearchDatmusic.Params(searchParams = params.searchParams.copy(page = page), forceRefresh = refreshing)
                    )
                } catch (error: Exception) {
                    onError(error)
                    throw error
                }
            },
            pagingSourceFactory = { dao.entriesPagingSource(params.searchParams) }
        ).flow
    }

    data class Params<T : PaginatedEntity>(
        val searchParams: DatmusicSearchParams,
        override val pagingConfig: PagingConfig = DEFAULT_PAGING_CONFIG,
    ) : Parameters<T>
}
