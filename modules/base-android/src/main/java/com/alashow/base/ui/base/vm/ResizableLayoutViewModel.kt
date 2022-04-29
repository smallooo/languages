/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.base.ui.base.vm

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import com.alashow.base.util.event
import com.alashow.data.PreferencesStore

open class ResizableLayoutViewModel @Inject constructor(
    preferencesStore: PreferencesStore,
    preferenceKey: Preferences.Key<Float>,
    defaultDragOffset: Float = 0f,
    private val analyticsPrefix: String,
    private val analytics: FirebaseAnalytics,
) : ViewModel() {

    private val dragOffsetState = preferencesStore.getStateFlow(
        preferenceKey, viewModelScope, defaultDragOffset,
        saveDebounce = 200
    )
    val dragOffset = dragOffsetState.asStateFlow()

    init {
        persistDragOffset()
    }

    fun setDragOffset(newOffset: Float) {
        viewModelScope.launch {
            dragOffsetState.value = newOffset
        }
    }

    private fun persistDragOffset() {
        viewModelScope.launch {
            dragOffsetState
                .debounce(2000)
                .collectLatest { analytics.event("$analyticsPrefix.resize", mapOf("offset" to it)) }
        }
    }
}
