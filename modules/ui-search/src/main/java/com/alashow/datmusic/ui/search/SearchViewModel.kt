/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import com.alashow.base.ui.SnackbarManager
import com.alashow.base.util.event
import com.alashow.base.util.extensions.getStateFlow
import com.alashow.base.util.extensions.stateInDefault
import com.alashow.datmusic.data.CaptchaSolution
import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.data.DatmusicSearchParams.BackendType
import com.alashow.datmusic.data.DatmusicSearchParams.Companion.withTypes
import com.alashow.datmusic.data.observers.search.ObservePagedDatmusicSearch
import com.alashow.datmusic.domain.entities.Album
import com.alashow.datmusic.domain.entities.Artist
import com.alashow.datmusic.domain.entities.Audio
import com.alashow.datmusic.domain.models.errors.ApiCaptchaError
import com.alashow.datmusic.playback.PlaybackConnection
import com.alashow.navigation.screens.QUERY_KEY
import com.alashow.navigation.screens.SEARCH_BACKENDS_KEY

const val SEARCH_DEBOUNCE_MILLIS = 400L

@OptIn(FlowPreview::class)
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val audiosPager: ObservePagedDatmusicSearch<Audio>,
    private val minervaPager: ObservePagedDatmusicSearch<Audio>,
    private val flacsPager: ObservePagedDatmusicSearch<Audio>,
    private val artistsPager: ObservePagedDatmusicSearch<Artist>,
    private val albumsPager: ObservePagedDatmusicSearch<Album>,
    private val snackbarManager: SnackbarManager,
    private val analytics: FirebaseAnalytics,
    private val playbackConnection: PlaybackConnection,
) : ViewModel() {

    private val initialQuery = handle.get(QUERY_KEY) ?: ""
    private val searchQueryState = handle.getStateFlow(initialQuery, viewModelScope, initialQuery)
    private val searchFilterState = handle.getStateFlow("search_filter", viewModelScope, SearchFilter.from(handle.get(SEARCH_BACKENDS_KEY)))
    private val searchTriggerState = handle.getStateFlow("search_trigger", viewModelScope, SearchTrigger(initialQuery))

    private val captchaError = MutableStateFlow<ApiCaptchaError?>(null)

    private val pendingActions = MutableSharedFlow<SearchAction>()

    val pagedAudioList get() = audiosPager.flow.cachedIn(viewModelScope)
    val pagedMinervaList get() = minervaPager.flow.cachedIn(viewModelScope)
    val pagedFlacsList get() = flacsPager.flow.cachedIn(viewModelScope)
    val pagedArtistsList get() = artistsPager.flow.cachedIn(viewModelScope)
    val pagedAlbumsList get() = albumsPager.flow.cachedIn(viewModelScope)

    private val onSearchEventChannel = Channel<SearchEvent>(Channel.CONFLATED)
    val onSearchEvent = onSearchEventChannel.receiveAsFlow()

    val state = combine(searchFilterState, snackbarManager.errors, captchaError, ::SearchViewState)
        .stateInDefault(viewModelScope, SearchViewState.Empty)

    val searchQuery = searchQueryState.asStateFlow()

    init {
        viewModelScope.launch {
            pendingActions.collectLatest { action ->
                when (action) {
                    is SearchAction.QueryChange -> {
                        searchQueryState.value = action.query

                        // trigger search while typing if minerva is the only backend selected
                        if (searchFilterState.value.hasMinervaOnly) {
                            searchTriggerState.value = SearchTrigger(searchQueryState.value)
                        }
                    }
                    is SearchAction.Search -> searchTriggerState.value = SearchTrigger(searchQueryState.value)
                    is SearchAction.SelectBackendType -> selectBackendType(action)
                    is SearchAction.SubmitCaptcha -> submitCaptcha(action)
                    is SearchAction.AddError -> snackbarManager.addError(action.error)
                    is SearchAction.ClearError -> snackbarManager.removeCurrentError()
                    is SearchAction.PlayAudio -> playAudio(action.audio)
                }
            }
        }

        viewModelScope.launch {
            combine(searchTriggerState, searchFilterState, ::SearchEvent)
                .debounce(SEARCH_DEBOUNCE_MILLIS)
                .collectLatest {
                    search(it)
                    onSearchEventChannel.send(it)
                }
        }

        listOf(audiosPager, minervaPager, flacsPager, artistsPager, albumsPager).forEach { pager ->
            pager.errors().watchForErrors(pager)
        }
    }

    fun search(searchEvent: SearchEvent) {
        val (trigger, filter) = searchEvent
        val query = trigger.query
        val searchParams = DatmusicSearchParams(query, trigger.captchaSolution)
        val backends = filter.backends.joinToString { it.type }

        Timber.d("Searching with query=$query, backends=$backends")
        analytics.event("search", mapOf("query" to query, "backends" to backends))

        if (filter.hasAudios)
            audiosPager(ObservePagedDatmusicSearch.Params(searchParams))

        if (filter.hasMinerva)
            minervaPager(ObservePagedDatmusicSearch.Params(searchParams.withTypes(BackendType.MINERVA), MINERVA_PAGING))

        if (filter.hasFlacs)
            flacsPager(ObservePagedDatmusicSearch.Params(searchParams.withTypes(BackendType.FLACS), MINERVA_PAGING))

        // don't send queries if backend can't handle empty queries
        if (query.isNotBlank()) {
            if (filter.hasArtists)
                artistsPager(ObservePagedDatmusicSearch.Params(searchParams.withTypes(BackendType.ARTISTS)))
            if (filter.hasAlbums)
                albumsPager(ObservePagedDatmusicSearch.Params(searchParams.withTypes(BackendType.ALBUMS)))
        }
    }

    fun submitAction(action: SearchAction) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }

    /**
     * Queue given audio to play with current query as the queue.
     */
    private fun playAudio(audio: Audio) {
        val query = searchTriggerState.value.query
        when {
            searchFilterState.value.hasMinerva -> playbackConnection.playWithMinervaQuery(query, audio.id)
            searchFilterState.value.hasFlacs -> playbackConnection.playWithFlacsQuery(query, audio.id)
            else -> playbackConnection.playWithQuery(query, audio.id)
        }
    }

    /**
     * Sets search filter to only given backend if [action.selected] otherwise resets to [SearchFilter.DefaultBackends].
     */
    private fun selectBackendType(action: SearchAction.SelectBackendType) {
        analytics.event("search.selectBackend", mapOf("type" to action.backendType))
        searchFilterState.value = searchFilterState.value.copy(
            backends = when (action.selected) {
                true -> setOf(action.backendType)
                else -> SearchFilter.DefaultBackends
            }
        )
    }

    /**
     * Resets captcha error and triggers search with given captcha solution.
     */
    private fun submitCaptcha(action: SearchAction.SubmitCaptcha) {
        captchaError.value = null
        searchTriggerState.value = SearchTrigger(
            query = searchQueryState.value,
            captchaSolution = CaptchaSolution(
                action.captchaError.error.captchaId,
                action.captchaError.error.captchaIndex,
                action.solution
            )
        )
    }

    private fun Flow<Throwable>.watchForErrors(pager: ObservePagedDatmusicSearch<*>) = viewModelScope.launch { collectErrors(pager) }

    private suspend fun Flow<Throwable>.collectErrors(pager: ObservePagedDatmusicSearch<*>) = collectLatest { error ->
        Timber.e(error, "Collected error from a pager")
        when (error) {
            is ApiCaptchaError -> captchaError.value = error
        }
    }

    companion object {
        val MINERVA_PAGING = PagingConfig(
            pageSize = 50,
            initialLoadSize = 50,
            prefetchDistance = 5,
            enablePlaceholders = true
        )
    }
}
