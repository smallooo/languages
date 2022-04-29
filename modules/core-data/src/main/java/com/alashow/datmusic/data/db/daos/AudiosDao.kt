/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import com.alashow.data.db.PaginatedEntryDao
import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.data.db.SQLITE_MAX_VARIABLES
import com.alashow.datmusic.domain.entities.Audio

@Dao
abstract class AudiosDao : PaginatedEntryDao<DatmusicSearchParams, Audio>() {

    @Transaction
    @Query("SELECT * FROM audios WHERE id IN (:ids) GROUP BY id")
    abstract suspend fun audiosById(ids: List<String>): List<Audio>

    @Transaction
    @Query("DELETE FROM audios WHERE id NOT IN (:ids)")
    abstract suspend fun deleteExcept(ids: List<String>): Int

    @Transaction
    @Query("DELETE FROM audios WHERE id NOT IN (:ids)")
    suspend fun deleteExceptChunked(ids: List<String>): Int {
        var deleted = 0
        for (chunk in ids.chunked(SQLITE_MAX_VARIABLES)) {
            deleted += deleteExcept(chunk)
        }
        return deleted
    }

    @Transaction
    @Query("SELECT * FROM audios ORDER BY page ASC, search_index ASC")
    abstract override fun entries(): Flow<List<Audio>>

    @Query("SELECT * FROM audios WHERE params = :params ORDER BY page ASC, search_index ASC")
    abstract override fun entries(params: DatmusicSearchParams): Flow<List<Audio>>

    @Query("SELECT * FROM audios WHERE params = :params and page = :page ORDER BY page ASC, search_index ASC")
    abstract override fun entries(params: DatmusicSearchParams, page: Int): Flow<List<Audio>>

    @Transaction
    @Query("SELECT * FROM audios ORDER BY page ASC, search_index ASC LIMIT :count OFFSET :offset")
    abstract override fun entries(count: Int, offset: Int): Flow<List<Audio>>

    @Transaction
    @Query("SELECT * FROM audios ORDER BY page ASC, search_index ASC")
    abstract override fun entriesPagingSource(): PagingSource<Int, Audio>

    @Transaction
    @Query("SELECT * FROM audios WHERE params = :params ORDER BY page ASC, search_index ASC")
    abstract override fun entriesPagingSource(params: DatmusicSearchParams): PagingSource<Int, Audio>

    @Transaction
    @Query("SELECT * FROM audios WHERE id = :id")
    abstract override fun entry(id: String): Flow<Audio>

    @Transaction
    @Query("SELECT * FROM audios WHERE id = :id")
    abstract override fun entryNullable(id: String): Flow<Audio?>

    @Transaction
    @Query("SELECT * FROM audios WHERE id IN (:ids)")
    abstract override fun entriesById(ids: List<String>): Flow<List<Audio>>

    @Query("DELETE FROM audios WHERE id = :id")
    abstract override suspend fun delete(id: String): Int

    @Query("DELETE FROM audios WHERE params = :params")
    abstract override suspend fun delete(params: DatmusicSearchParams): Int

    @Query("DELETE FROM audios WHERE params = :params and page = :page")
    abstract override suspend fun delete(params: DatmusicSearchParams, page: Int): Int

    @Query("DELETE FROM audios")
    abstract override suspend fun deleteAll(): Int

    @Query("SELECT COUNT(*) from audios")
    abstract override suspend fun count(): Int

    @Query("SELECT COUNT(*) from audios")
    abstract override fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) from audios where params = :params")
    abstract override suspend fun count(params: DatmusicSearchParams): Int

    @Query("SELECT COUNT(*) from audios where id = :id")
    abstract override suspend fun exists(id: String): Int

    @Query("SELECT COUNT(*) from audios where id = :id")
    abstract override fun has(id: String): Flow<Int>
}
