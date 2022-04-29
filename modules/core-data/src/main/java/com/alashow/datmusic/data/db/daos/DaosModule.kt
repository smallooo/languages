/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.db.daos

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.alashow.data.db.PaginatedEntryDao
import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.data.db.AppDatabase
import com.alashow.datmusic.domain.entities.Album
import com.alashow.datmusic.domain.entities.Artist
import com.alashow.datmusic.domain.entities.Audio

@InstallIn(SingletonComponent::class)
@Module
class DaosModule {
    @Provides
    fun audiosDao(db: AppDatabase) = db.audiosDao()

    @Provides
    fun audiosDaoBase(db: AppDatabase): PaginatedEntryDao<DatmusicSearchParams, Audio> = db.audiosDao()

    @Provides
    fun audiosFtsDao(db: AppDatabase) = db.audiosFtsDao()

    @Provides
    fun artistsDao(db: AppDatabase) = db.artistsDao()

    @Provides
    fun artistsDaoBase(db: AppDatabase): PaginatedEntryDao<DatmusicSearchParams, Artist> = db.artistsDao()

    @Provides
    fun albumsDao(db: AppDatabase) = db.albumsDao()

    @Provides
    fun albumsDaoBase(db: AppDatabase): PaginatedEntryDao<DatmusicSearchParams, Album> = db.albumsDao()

    @Provides
    fun playlistsDao(db: AppDatabase) = db.playlistsDao()

    @Provides
    fun playlistsWithAudiosDao(db: AppDatabase) = db.playlistsWithAudiosDao()

    @Provides
    fun downloadRequestsDao(db: AppDatabase) = db.downloadRequestsDao()
}
