/*
 * Copyright (C) 2022, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.observers.playlist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.alashow.data.SubjectInteractor
import com.alashow.datmusic.data.interactors.playlist.SearchPlaylistItems
import com.alashow.datmusic.data.repos.playlist.PlaylistsRepo
import com.alashow.datmusic.domain.entities.PlaylistId
import com.alashow.datmusic.domain.entities.PlaylistItem
import com.alashow.datmusic.domain.entities.PlaylistItems
import com.alashow.domain.models.Async
import com.alashow.domain.models.Fail
import com.alashow.domain.models.Success

class ObservePlaylistDetails @Inject constructor(
    private val searchPlaylistItems: SearchPlaylistItems,
    private val playlistsRepo: PlaylistsRepo,
) : SubjectInteractor<ObservePlaylistDetails.Params, PlaylistItems>() {

    data class Params(
        val playlistId: PlaylistId = 0,
        val query: String = "",
        val defaultSortOption: PlaylistItemSortOption = PlaylistItemSortOptions.ALL.first(),
        val sortOptions: List<PlaylistItemSortOption> = PlaylistItemSortOptions.ALL,
        val sortOption: PlaylistItemSortOption = defaultSortOption,
    ) {
        val hasQuery get() = query.isNotBlank()
        val hasSortingOption get() = sortOption != defaultSortOption
        val hasNoFilters get() = !hasQuery && !hasSortingOption
    }

    override fun createObservable(params: Params): Flow<List<PlaylistItem>> {
        val playlistItems = when (params.hasQuery) {
            true -> searchPlaylistItems(SearchPlaylistItems.Params(params.playlistId, params.query))
            false -> playlistsRepo.playlistItems(params.playlistId)
        }
        return playlistItems.map {
            val comparator = params.sortOption.comparator
            if (comparator != null) it.sortedWith(comparator)
            else it
        }
    }
}

fun Async<List<PlaylistItem>>.failWithNoResultsIfEmpty(params: ObservePlaylistDetails.Params) = let {
    if (it is Success) {
        if (it().isEmpty() && params.hasQuery)
            Fail(NoResultsForPlaylistFilter(params))
        else it
    } else it
}
