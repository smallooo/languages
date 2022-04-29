/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.domain.entities.AlbumId
import com.alashow.datmusic.domain.entities.ArtistId
import com.alashow.datmusic.domain.models.ApiResponse

interface DatmusicEndpoints {

    @JvmSuppressWildcards
    @GET("/multisearch")
    suspend fun multisearch(@QueryMap params: Map<String, Any>, @Query("types[]") vararg types: DatmusicSearchParams.BackendType): ApiResponse

    @JvmSuppressWildcards
    @GET("/search/artists")
    suspend fun searchArtists(@QueryMap params: Map<String, Any>, @Query("types[]") vararg types: DatmusicSearchParams.BackendType): ApiResponse

    @JvmSuppressWildcards
    @GET("/search/albums")
    suspend fun searchAlbums(@QueryMap params: Map<String, Any>, @Query("types[]") vararg types: DatmusicSearchParams.BackendType): ApiResponse

    @JvmSuppressWildcards
    @GET("/artists/{id}")
    suspend fun artist(@Path("id") id: ArtistId, @QueryMap params: Map<String, Any>): ApiResponse

    @JvmSuppressWildcards
    @GET("/albums/{id}")
    suspend fun album(@Path("id") id: AlbumId, @QueryMap params: Map<String, Any>): ApiResponse

    @GET("/bytes/{searchKey}/{audioId}")
    suspend fun bytes(@Path("searchKey") searchKey: String, @Path("audioId") audioId: String): ApiResponse

    @POST("/users/register/fcm")
    @FormUrlEncoded
    suspend fun registerFcmToken(@Field("token") token: String): ApiResponse
}
