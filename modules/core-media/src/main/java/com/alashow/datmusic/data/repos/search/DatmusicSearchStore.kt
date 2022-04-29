/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.repos.search

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.Duration
import com.alashow.data.LastRequests
import com.alashow.data.PreferencesStore
import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.data.db.daos.AlbumsDao
import com.alashow.datmusic.data.db.daos.ArtistsDao
import com.alashow.datmusic.data.db.daos.AudiosDao
import com.alashow.datmusic.domain.entities.Album
import com.alashow.datmusic.domain.entities.Artist
import com.alashow.datmusic.domain.entities.Audio
import com.alashow.datmusic.domain.models.errors.requireNonEmpty

typealias DatmusicSearchStore<T> = Store<DatmusicSearchParams, List<T>>

@InstallIn(SingletonComponent::class)
@Module
object DatmusicSearchStoreModule {

    private fun searchPrimaryKey(id: Any, params: DatmusicSearchParams) = "${id}_${params.toString().hashCode()}"

    private suspend fun <T> Result<List<T>>.fetcherDefaults(lastRequests: LastRequests, params: DatmusicSearchParams) = onSuccess {
        lastRequests.save(params = params.toString() + params.page)
    }.requireNonEmpty()

    private fun <T> Flow<List<T>>.sourceReaderFilter(lastRequests: LastRequests, params: DatmusicSearchParams) = map { entries ->
        when {
            entries.isEmpty() -> null // because Store only treats nulls as no-value
            lastRequests.isExpired(params = params.toString() + params.page) -> null // this source is invalid if it's expired
            else -> entries
        }
    }

    @Provides
    @Singleton
    fun datmusicSearchAudioStore(
        search: DatmusicSearchDataSource,
        dao: AudiosDao,
        @Named("audios") lastRequests: LastRequests
    ): DatmusicSearchStore<Audio> = StoreBuilder.from(
        fetcher = Fetcher.of { params: DatmusicSearchParams ->
            search(params).map { it.data.audios + it.data.minerva + it.data.flacs }
                .fetcherDefaults(lastRequests, params)
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { params -> dao.entries(params, params.page).sourceReaderFilter(lastRequests, params) },
            writer = { params, response ->
                dao.withTransaction {
                    val entries = response.mapIndexed { index, it ->
                        it.copy(
                            params = params.toString(),
                            page = params.page,
                            searchIndex = index,
                            primaryKey = searchPrimaryKey(it.id, params),
                        )
                    }
                    dao.update(params, params.page, entries)
                }
            },
            delete = dao::delete,
            deleteAll = dao::deleteAll
        )
    ).build()

    @Provides
    @Singleton
    fun datmusicSearchArtistsStore(
        search: DatmusicSearchDataSource,
        dao: ArtistsDao,
        @Named("artists") lastRequests: LastRequests
    ): DatmusicSearchStore<Artist> = StoreBuilder.from(
        fetcher = Fetcher.of { params: DatmusicSearchParams -> search(params).map { it.data.artists }.fetcherDefaults(lastRequests, params) },
        sourceOfTruth = SourceOfTruth.of(
            reader = { params -> dao.entries(params, params.page).sourceReaderFilter(lastRequests, params) },
            writer = { params, response ->
                dao.withTransaction {
                    val entries =
                        response.mapIndexed { index, it ->
                            it.copy(
                                params = params.toString(),
                                page = params.page,
                                searchIndex = index,
                                primaryKey = searchPrimaryKey(it.id, params),
                            )
                        }
                    dao.update(params, params.page, entries)
                }
            },
            delete = dao::delete,
            deleteAll = dao::deleteAll
        )
    ).build()

    @Provides
    @Singleton
    fun datmusicSearchAlbumsStore(
        search: DatmusicSearchDataSource,
        dao: AlbumsDao,
        @Named("albums") lastRequests: LastRequests
    ): DatmusicSearchStore<Album> = StoreBuilder.from(
        fetcher = Fetcher.of { params: DatmusicSearchParams -> search(params).map { it.data.albums }.fetcherDefaults(lastRequests, params) },
        sourceOfTruth = SourceOfTruth.of(
            reader = { params -> dao.entries(params, params.page).sourceReaderFilter(lastRequests, params) },
            writer = { params, response ->
                dao.withTransaction {
                    val entries =
                        response.mapIndexed { index, it ->
                            it.copy(
                                params = params.toString(),
                                page = params.page,
                                searchIndex = index,
                                primaryKey = searchPrimaryKey(it.id, params),
                            )
                        }
                    dao.update(params, params.page, entries)
                }
            },
            delete = dao::delete,
            deleteAll = dao::deleteAll
        )
    ).build()

    @Provides
    @Singleton
    @Named("audios")
    fun searchAudiosLastRequests(preferences: PreferencesStore) = LastRequests("search_audios", preferences)

    @Provides
    @Singleton
    @Named("artists")
    fun searchArtistsLastRequests(preferences: PreferencesStore) = LastRequests("search_artists", preferences, Duration.ofDays(7))

    @Provides
    @Singleton
    @Named("albums")
    fun searchAlbumsLastRequests(preferences: PreferencesStore) = LastRequests("search_albums", preferences, Duration.ofDays(7))
}
