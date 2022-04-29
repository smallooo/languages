/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import com.alashow.base.util.extensions.stateInDefault
import com.alashow.data.REMOTE_CONFIG_FETCH_DELAY
import com.alashow.data.RemoteConfig
import com.alashow.datmusic.data.config.getSettingsLinks

@HiltViewModel
class SettingsViewModel @Inject constructor(
    handle: SavedStateHandle,
    remoteConfig: RemoteConfig,
) : ViewModel() {

    val settingsLinks = flow {
        // initially fetch once then one more time when there might be an update
        emit(remoteConfig.getSettingsLinks())
        delay(REMOTE_CONFIG_FETCH_DELAY)
        emit(remoteConfig.getSettingsLinks())
    }.stateInDefault(viewModelScope, emptyList())
}
