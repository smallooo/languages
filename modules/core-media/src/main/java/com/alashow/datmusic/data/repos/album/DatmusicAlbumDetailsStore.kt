/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.repos.album

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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import com.alashow.data.LastRequests
import com.alashow.data.PreferencesStore
import com.alashow.datmusic.data.DatmusicAlbumParams
import com.alashow.datmusic.data.db.daos.AlbumsDao
import com.alashow.datmusic.data.db.daos.AudiosDao
import com.alashow.datmusic.domain.entities.Album
import com.alashow.datmusic.domain.entities.Audio

typealias DatmusicAlbumDetailsStore = Store<DatmusicAlbumParams, List<Audio>>

@InstallIn(SingletonComponent::class)
@Module
object DatmusicAlbumDetailsStoreModule {

    private suspend fun <T> Result<T>.fetcherDefaults(lastRequests: LastRequests, params: DatmusicAlbumParams) =
        onSuccess { lastRequests.save(params.toString()) }
            .onFailure { Timber.e(it) }
            .getOrThrow()

    private fun Flow<Album?>.sourceReaderFilter(lastRequests: LastRequests, params: DatmusicAlbumParams) = map { entry ->
        when (entry != null) {
            true -> {
                when {
                    !entry.detailsFetched -> null
                    lastRequests.isExpired(params.toString()) -> null
                    else -> entry.audios
                }
            }
            else -> null
        }
    }

    @Provides
    @Singleton
    fun datmusicAlbumDetailsStore(
        albums: DatmusicAlbumDataSource,
        dao: AlbumsDao,
        audiosDao: AudiosDao,
        @Named("album_details") lastRequests: LastRequests
    ): DatmusicAlbumDetailsStore = StoreBuilder.from(
        fetcher = Fetcher.of { params: DatmusicAlbumParams ->
            albums(params).map { it.data.album to it.data.audios }.fetcherDefaults(lastRequests, params)
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { params: DatmusicAlbumParams -> dao.entry(params.id.toString()).sourceReaderFilter(lastRequests, params) },
            writer = { params, (newEntry, audios) ->
                dao.withTransaction {
                    val entry = dao.entry(params.id.toString()).firstOrNull() ?: newEntry
                    dao.updateOrInsert(
                        entry.copy(
                            audios = audios,
                            detailsFetched = true,
                            year = newEntry.year,
                        )
                    )
                    audiosDao.insertMissing(audios.mapIndexed { index, audio -> audio.copy(primaryKey = audio.id, searchIndex = index) })
                }
            },
            delete = { error("This store doesn't manage deletes") },
            deleteAll = { error("This store doesn't manage deleteAll") },
        )
    ).build()

    @Provides
    @Singleton
    @Named("album_details")
    fun datmusicAlbumDetailsLastRequests(preferences: PreferencesStore) = LastRequests("album_details", preferences)
}
