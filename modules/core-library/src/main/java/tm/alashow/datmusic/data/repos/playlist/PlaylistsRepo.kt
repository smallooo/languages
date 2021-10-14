/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package tm.alashow.datmusic.data.repos.playlist

import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import tm.alashow.base.imageloading.getBitmap
import tm.alashow.base.util.CoroutineDispatchers
import tm.alashow.data.db.RoomRepo
import tm.alashow.datmusic.data.db.daos.PlaylistsDao
import tm.alashow.datmusic.data.db.daos.PlaylistsWithAudiosDao
import tm.alashow.datmusic.data.repos.playlist.PlaylistArtworkUtils.savePlaylistArtwork
import tm.alashow.datmusic.domain.entities.CoverImageSize
import tm.alashow.datmusic.domain.entities.PLAYLIST_NAME_MAX_LENGTH
import tm.alashow.datmusic.domain.entities.Playlist
import tm.alashow.datmusic.domain.entities.PlaylistAudio
import tm.alashow.datmusic.domain.entities.PlaylistId
import tm.alashow.datmusic.domain.entities.PlaylistItems
import tm.alashow.datmusic.domain.entities.PlaylistWithAudios
import tm.alashow.i18n.DatabaseInsertError
import tm.alashow.i18n.DatabaseValidationNotFound
import tm.alashow.i18n.ValidationErrorBlank
import tm.alashow.i18n.ValidationErrorTooLong

class PlaylistsRepo @Inject constructor(
    private val context: Context,
    private val dispatchers: CoroutineDispatchers,
    private val dao: PlaylistsDao,
    private val playlistAudiosDao: PlaylistsWithAudiosDao,
) : RoomRepo<PlaylistId, Playlist>(dao, dispatchers), CoroutineScope by ProcessLifecycleOwner.get().lifecycleScope {

    private suspend fun validatePlaylistId(playlistId: PlaylistId) {
        if (!exists(playlistId)) {
            Timber.e("Playlist with id: $playlistId doesn't exist")
            throw DatabaseValidationNotFound.error()
        }
    }

    private fun validatePlaylist(playlist: Playlist) {
        if (playlist.name.isBlank()) {
            throw ValidationErrorBlank().error()
        }
        if (playlist.name.length > PLAYLIST_NAME_MAX_LENGTH) {
            throw ValidationErrorTooLong().error()
        }
    }

    suspend fun createPlaylist(playlist: Playlist, audioIds: List<String> = listOf()): PlaylistId {
        validatePlaylist(playlist)

        var playlistId: PlaylistId
        withContext(dispatchers.io) {
            playlistId = dao.insert(playlist)
            if (playlistId < 0)
                throw DatabaseInsertError.error()

            addAudiosToPlaylist(playlistId, audioIds)
        }

        return playlistId
    }

    suspend fun updatePlaylist(playlist: Playlist): Playlist {
        validatePlaylist(playlist)
        val updatedPlaylist: Playlist
        withContext(dispatchers.io) {
            updatedPlaylist = update(playlist.updatedCopy())
        }

        return updatedPlaylist
    }

    suspend fun addAudiosToPlaylist(playlistId: PlaylistId, audioIds: List<String>): List<PlaylistId> {
        val insertedIds = mutableListOf<PlaylistId>()
        withContext(dispatchers.io) {
            validatePlaylistId(playlistId)

            val lastIndex = playlistAudiosDao.lastPlaylistAudioIndex(playlistId).firstOrNull() ?: -1
            val playlistWithAudios = audioIds.mapIndexed { index, id ->
                PlaylistAudio(
                    playlistId = playlistId,
                    audioId = id,
                    position = lastIndex + (index + 1)
                )
            }
            insertedIds += playlistAudiosDao.insertAll(playlistWithAudios)
            generatePlaylistArtwork(playlistId)
            return@withContext
        }
        return insertedIds
    }

    suspend fun swapPositions(playlistId: PlaylistId, from: Int, to: Int) {
        withContext(dispatchers.io) {
            validatePlaylistId(playlistId)

            val fromAudio = playlistAudiosDao.getByPosition(playlistId, from)
            val toAudio = playlistAudiosDao.getByPosition(playlistId, to)

            playlistAudiosDao.updatePosition(fromAudio.id, toPosition = to)
            playlistAudiosDao.updatePosition(toAudio.id, toPosition = from)
            generatePlaylistArtwork(playlistId)
        }
    }

    suspend fun updatePlaylistItems(playlistItems: PlaylistItems) {
        if (playlistItems.isEmpty()) {
            return
        }
        val playlistId = playlistItems.first().playlistAudio.playlistId
        playlistAudiosDao.updateAll(playlistItems.map { it.playlistAudio })
        if (playlistItems.isNotEmpty())
            generatePlaylistArtwork(playlistId)
    }

    suspend fun deletePlaylistItems(playlistItems: PlaylistItems): Int {
        return playlistAudiosDao.deletePlaylistItems(playlistItems.map { it.playlistAudio.id })
    }

    private fun audiosOfPlaylist(id: PlaylistId) = playlistAudiosDao.audiosOfPlaylist(id).map { it.map { item -> item.audio } }
    fun playlist(id: PlaylistId) = dao.entry(id)
    fun playlistAudios(id: PlaylistId) = playlistAudiosDao.playlistItems(id)
    fun playlistWithAudios(id: PlaylistId) = combine(playlist(id), audiosOfPlaylist(id), ::PlaylistWithAudios)

    fun playlists() = dao.entries()
    fun playlistsWithAudios() = playlistAudiosDao.playlistsWithAudios()

    override suspend fun delete(id: PlaylistId): Int {
        validatePlaylistId(id)
        playlist(id).first().artworkFile()?.delete()
        return super.delete(id)
    }

    override suspend fun clear(): Int {
        ArtworkImageFolderType.PLAYLIST.getArtworkImageFolder(context).delete()
        return super.clear()
    }

    private fun generatePlaylistArtwork(playlistId: PlaylistId, maxArtworksNeeded: Int = 4) {
        launch(dispatchers.computation) {
            validatePlaylistId(playlistId)
            val playlist = playlist(playlistId).first().updatedCopy()
            val playlistAudios = audiosOfPlaylist(playlistId).first()

            if (playlistAudios.isNotEmpty()) {
                Timber.i("Considering to auto generate artwork for playlist id=$playlistId")
                val artworkUrls = playlistAudios.map { it.coverUri(CoverImageSize.LARGE, allowAlternate = false) }
                    .filter { it.toString().isNotBlank() }
                    .toSet()
                    .take(maxArtworksNeeded)

                val artworksHash = artworkUrls.hashCode().toString()

                if (artworksHash == playlist.artworkSource) {
                    Timber.i("Skipping generating artwork for id=$playlistId because it has the same hash = $artworksHash")
                    return@launch
                }

                val artworkBitmaps = artworkUrls.mapNotNull { context.getBitmap(it, allowHardware = false) }

                if (artworkBitmaps.isNotEmpty()) {
                    playlist.artworkFile()?.delete()
                    val merged = PlaylistArtworkUtils.joinImages(artworkBitmaps)
                    val file = playlistId.savePlaylistArtwork(context, merged, ArtworkImageFileType.PLAYLIST_AUTO_GENERATED)

                    Timber.d("Auto-generated artwork for playlist: hash=$artworksHash, file=$file, from urls=${artworkUrls.joinToString()}")

                    val updatedPlaylist = playlist.copy(artworkPath = file.path, artworkSource = artworksHash)
                    updatePlaylist(updatedPlaylist)
                } else {
                    Timber.w("Playlist id=$playlistId doesn't have any audios with artwork")
                }
            } else {
                Timber.w("Playlist id=$playlistId is empty, cannot generate artwork")
            }
        }
    }
}