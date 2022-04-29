/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.observers.artist

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.alashow.data.SubjectInteractor
import com.alashow.datmusic.data.DatmusicArtistParams
import com.alashow.datmusic.data.db.daos.ArtistsDao
import com.alashow.datmusic.data.interactors.artist.GetArtistDetails
import com.alashow.datmusic.domain.entities.Artist

class ObserveArtist @Inject constructor(
    private val artistsDao: ArtistsDao,
) : SubjectInteractor<DatmusicArtistParams, Artist>() {
    override fun createObservable(params: DatmusicArtistParams): Flow<Artist> = artistsDao.entry(params.id)
}

class ObserveArtistDetails @Inject constructor(
    private val getArtistDetails: GetArtistDetails,
) : SubjectInteractor<GetArtistDetails.Params, Artist>() {
    override fun createObservable(params: GetArtistDetails.Params) = getArtistDetails(params)
}
