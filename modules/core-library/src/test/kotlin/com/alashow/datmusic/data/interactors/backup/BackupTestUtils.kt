/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.interactors.backup

import com.alashow.datmusic.data.SampleData
import com.alashow.datmusic.data.db.daos.AlbumsDao
import com.alashow.datmusic.data.db.daos.ArtistsDao
import com.alashow.datmusic.data.db.daos.DownloadRequestsDao
import com.alashow.datmusic.data.repos.audio.AudiosRepo
import com.alashow.datmusic.data.repos.playlist.PlaylistsRepo
import com.alashow.datmusic.domain.entities.Audio
import com.alashow.datmusic.domain.entities.DownloadRequest

internal suspend fun createBackupData(
    playlistsRepo: PlaylistsRepo,
    audiosRepo: AudiosRepo,
    artistsDao: ArtistsDao,
    albumsDao: AlbumsDao,
    downloadRequestsDao: DownloadRequestsDao,
): Pair<List<Audio>, List<DownloadRequest>> {
    val audioItems = (1..10).map { SampleData.audio() }
        .also { audiosRepo.insertAll(it) }
    val artistItems = (1..10).map { SampleData.artist() }
        .also { artistsDao.insertAll(it) }
    val albumItems = (1..10).map { SampleData.album() }
        .also { albumsDao.insertAll(it) }
    val playlistItemAudios = audioItems.shuffled().take(5)
    val playlist = SampleData.playlist()
        .also { playlistsRepo.createPlaylist(it, playlistItemAudios.map { it.id }) }
    val downloadRequests = audioItems.map { SampleData.downloadRequest(it) }
        .also { audiosRepo.insertAll(it.map { it.audio }) }
        .also { downloadRequestsDao.insertAll(it) }

    return Pair(playlistItemAudios, downloadRequests)
}
