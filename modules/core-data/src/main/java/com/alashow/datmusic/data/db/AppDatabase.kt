/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alashow.datmusic.data.db.daos.AlbumsDao
import com.alashow.datmusic.data.db.daos.ArtistsDao
import com.alashow.datmusic.data.db.daos.AudiosDao
import com.alashow.datmusic.data.db.daos.AudiosFtsDao
import com.alashow.datmusic.data.db.daos.DownloadRequestsDao
import com.alashow.datmusic.data.db.daos.PlaylistsDao
import com.alashow.datmusic.data.db.daos.PlaylistsWithAudiosDao
import com.alashow.datmusic.domain.entities.Album
import com.alashow.datmusic.domain.entities.Artist
import com.alashow.datmusic.domain.entities.Audio
import com.alashow.datmusic.domain.entities.AudioFts
import com.alashow.datmusic.domain.entities.DownloadRequest
import com.alashow.datmusic.domain.entities.Playlist
import com.alashow.datmusic.domain.entities.PlaylistAudio
import com.alashow.domain.models.BaseTypeConverters

const val SQLITE_MAX_VARIABLES = 900

@Database(
    version = 13,
    entities = [
        Audio::class,
        AudioFts::class,
        Artist::class,
        Album::class,
        DownloadRequest::class,
        Playlist::class,
        PlaylistAudio::class,
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 4, to = 5, spec = PlaylistRenameIdMigration::class),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8, spec = AlbumDeleteOldColumnsMigration::class),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10, spec = AlbumDeleteAlbumIdColumnMigration::class),
        AutoMigration(from = 10, to = 11, spec = RenameAlbumOwnerIdToArtistIdColumnMigration::class),
        AutoMigration(from = 11, to = 12, spec = DownloadRequestDeleteEntityIdColumnMigration::class),
        AutoMigration(from = 12, to = 13),
    ]
)
@TypeConverters(BaseTypeConverters::class, AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun audiosDao(): AudiosDao
    abstract fun audiosFtsDao(): AudiosFtsDao

    abstract fun artistsDao(): ArtistsDao
    abstract fun albumsDao(): AlbumsDao

    abstract fun playlistsDao(): PlaylistsDao
    abstract fun playlistsWithAudiosDao(): PlaylistsWithAudiosDao

    abstract fun downloadRequestsDao(): DownloadRequestsDao
}
