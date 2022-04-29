/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.playback

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.alashow.base.ui.base.vm.ResizableLayoutViewModel
import com.alashow.data.PreferencesStore

private val PlaybackSheetLayoutDragOffsetKey = floatPreferencesKey("PlaybackSheetLayoutDragOffsetKey")

@HiltViewModel
class ResizablePlaybackSheetLayoutViewModel @Inject constructor(
    handle: SavedStateHandle,
    preferencesStore: PreferencesStore,
    analytics: FirebaseAnalytics,
) : ResizableLayoutViewModel(
    preferencesStore = preferencesStore,
    analytics = analytics,
    preferenceKey = PlaybackSheetLayoutDragOffsetKey,
    defaultDragOffset = 0f,
    analyticsPrefix = "playbackSheet.layout"
)
