/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package tm.alashow.datmusic.data.db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import tm.alashow.data.db.PaginatedEntryDao
import tm.alashow.datmusic.data.repos.search.DatmusicSearchParams
import tm.alashow.datmusic.domain.entities.Audio

@Dao
abstract class AudiosDao : PaginatedEntryDao<DatmusicSearchParams, Audio>() {

    @Transaction
    @Query("SELECT * FROM audios WHERE params = :params")
    abstract override fun entriesObservable(params: DatmusicSearchParams): Flow<List<Audio>>

    @Transaction
    @Query("SELECT * FROM audios ORDER BY page ASC LIMIT :count OFFSET :offset")
    abstract override fun entriesObservable(count: Int, offset: Int): Flow<List<Audio>>

    @Transaction
    @Query("SELECT * FROM audios ORDER BY page ASC")
    abstract override fun entriesPagingSource(): PagingSource<Int, Audio>

    @Transaction
    @Query("SELECT * FROM audios WHERE id = :id")
    abstract override fun entry(id: String): Flow<Audio>

    @Query("DELETE FROM audios WHERE id = :id")
    abstract override suspend fun delete(id: String)

    @Query("DELETE FROM audios WHERE params = :params")
    abstract override suspend fun delete(params: DatmusicSearchParams)

    @Query("DELETE FROM audios")
    abstract override suspend fun deleteAll()

    @Query("SELECT MAX(page) from audios WHERE params = :params")
    abstract override suspend fun getLastPage(params: DatmusicSearchParams): Int?

    @Query("SELECT COUNT(*) from audios where params = :params")
    abstract override suspend fun count(params: DatmusicSearchParams): Int

    @Query("SELECT COUNT(*) from audios where id = :id")
    abstract override suspend fun has(id: String): Int
}