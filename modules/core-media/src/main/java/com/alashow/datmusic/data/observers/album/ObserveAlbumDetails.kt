/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.observers.album

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.alashow.data.SubjectInteractor
import com.alashow.datmusic.data.DatmusicAlbumParams
import com.alashow.datmusic.data.db.daos.AlbumsDao
import com.alashow.datmusic.data.interactors.album.GetAlbumDetails
import com.alashow.datmusic.domain.entities.Album
import com.alashow.datmusic.domain.entities.Audios

class ObserveAlbum @Inject constructor(
    private val albumsDao: AlbumsDao,
) : SubjectInteractor<DatmusicAlbumParams, Album>() {
    override fun createObservable(params: DatmusicAlbumParams): Flow<Album> = albumsDao.entry(params.id)
}

class ObserveAlbumDetails @Inject constructor(
    private val getAlbumDetails: GetAlbumDetails,
) : SubjectInteractor<GetAlbumDetails.Params, Audios>() {

    override fun createObservable(params: GetAlbumDetails.Params) = getAlbumDetails(params)
}
