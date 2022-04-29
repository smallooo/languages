/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.playback

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.alashow.datmusic.playback.PlaybackConnection
import com.alashow.datmusic.playback.SET_MEDIA_STATE

@HiltViewModel
class PlaybackConnectionViewModel @Inject constructor(
    val playbackConnection: PlaybackConnection,
    private val handle: SavedStateHandle,
) : ViewModel() {

    init {
        viewModelScope.launch {
            playbackConnection.isConnected.collectLatest { connected ->
                if (connected) {
                    playbackConnection.transportControls?.sendCustomAction(SET_MEDIA_STATE, null)
                }
            }
        }
    }
}
